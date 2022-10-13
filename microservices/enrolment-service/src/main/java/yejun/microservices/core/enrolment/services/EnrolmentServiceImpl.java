package yejun.microservices.core.enrolment.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yejun.api.common.Department;
import yejun.api.common.MessageSources;
import yejun.api.common.Semester;
import yejun.api.common.ServiceAddresses;
import yejun.api.course.Course;
import yejun.api.enrolment.*;
import yejun.api.event.Event;
import yejun.api.student.Student;
import yejun.microservices.core.enrolment.persistence.EnrolmentEntity;
import yejun.microservices.core.enrolment.persistence.EnrolmentRepository;
import yejun.util.exceptions.BadRequestException;
import yejun.util.exceptions.InvalidInputException;
import yejun.util.http.ServiceUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@EnableBinding(MessageSources.class)
@CrossOrigin
public class EnrolmentServiceImpl implements EnrolmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EnrolmentServiceImpl.class);

    private final String studentServiceUrl = "http://student";

    private final String courseServiceUrl = "http://course";

    private final ServiceUtil serviceUtil;

    private final EnrolmentRepository repository;

    private final EnrolmentMapper mapper;

    private final WebClient.Builder webClientBuilder;

    private final MessageSources messageSources;

    private WebClient webClient;


    @Autowired
    public EnrolmentServiceImpl(ServiceUtil serviceUtil, EnrolmentRepository repository, EnrolmentMapper mapper, WebClient.Builder webClientBuilder, MessageSources messageSources) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
        this.webClientBuilder = webClientBuilder;
        this.messageSources = messageSources;
    }

    public WebClient getWebClient() {
        if (webClient == null)
            webClient = webClientBuilder.build();
        return webClient;
    }

    @Override
    public Mono<Enrolment> createEnrolment(Long courseId) {
        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        Enrolment enrolment = new Enrolment(null, courseId, null);
        EnrolmentEntity entity = mapper.apiToEntity(enrolment);

        Mono<Enrolment> newEntity = repository.save(entity)
                .log(null, FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Course Id: " + courseId))
                .map(mapper::entityToApi);

        return newEntity;

    }

    @Override
    public Mono<EnrolmentByCourse> getEnrolmentByCourse(HttpHeaders headers, Long courseId, int delay, int faultPercent) {
        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        if (delay > 0) simulateDelay(delay);

        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);

        LOG.info("Will get Enrolment info for course id={}", courseId);

        Flux<Enrolment> enrolmentFlux = repository.findAllByCourseIdAndStudentIdIsNotNull(courseId)
                .log(null, FINE)
                .map(e -> mapper.entityToApi(e))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
        List<Integer> studentIds = enrolmentFlux.toStream().map(Enrolment::getStudentId).collect(Collectors.toList());
        URI url = UriComponentsBuilder.fromUriString(studentServiceUrl + "/student").queryParam("studentIds", studentIds).build().encode().toUri();

        LOG.debug("Will call the getStudent API on URL: {}", url);

        Flux<Student> studentFlux = getWebClient().get().uri(url).headers(h -> h.addAll(headers)).retrieve().bodyToFlux(Student.class).log(null, FINE).onErrorResume(error -> empty());
        List<Student> studentList = studentFlux.collectList().block();
        String studentAddress = studentList.get(0).getServiceAddress();
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceUtil.getServiceAddress(),null,studentAddress);

        EnrolmentByCourse enrolmentByCourse = new EnrolmentByCourse(courseId, studentList.stream().map(student -> mapper.studentApiToSummary(student)).collect(Collectors.toList()), serviceAddresses);

        return Mono.just(enrolmentByCourse);
    }

    @Override
    public Mono<EnrolmentByStudent> getEnrolmentByStudent(HttpHeaders headers, EnrolmentStudentDTO enrolmentStudentDTO) {
        EnrolmentByStudent enrolmentByStudent = new EnrolmentByStudent();
        enrolmentByStudent.setStudentId(enrolmentStudentDTO.getStudentId());
        CourseSummary courseSummary = new CourseSummary();
        courseSummary.setCourseId(2150685201L);
        courseSummary.setCapacity(40);
        courseSummary.setSpare(15);
        courseSummary.setNumberOfStudents(25);
        courseSummary.setProfessorName("김영한");
        courseSummary.setCredit(3);
        courseSummary.setDepartment(Department.IT_CONVERGENCE);
        courseSummary.setSemester(enrolmentStudentDTO.getSemester());
        courseSummary.setTitle("클라우드융합");
        courseSummary.setYear(enrolmentStudentDTO.getYear());

        CourseSummary course2 = new CourseSummary();
        course2.setCourseId(65685201L);
        course2.setCapacity(50);
        course2.setSpare(1);
        course2.setNumberOfStudents(49);
        course2.setProfessorName("Anjolinya Jolyeo");
        course2.setCredit(4);
        course2.setDepartment(Department.SCHOOL_OF_BUSINESS_ADMINISTRATION);
        course2.setSemester(enrolmentStudentDTO.getSemester());
        course2.setTitle("Zip e ga go ship da");
        course2.setYear(enrolmentStudentDTO.getYear());

        List<CourseSummary> courseSummaryList = new ArrayList<>();
        courseSummaryList.add(courseSummary);
        courseSummaryList.add(course2);

        enrolmentByStudent.setCourses(courseSummaryList);
        return Mono.just(enrolmentByStudent);

//        Integer studentId = enrolmentStudentDTO.getStudentId();
//        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);
//
//
//
//        LOG.info("Will get Enrolment info for student id={}", studentId);
//
//        Flux<Enrolment> enrolmentFlux = repository.findAllByStudentId(studentId)
//                .log(null, FINE)
//                .map(e -> mapper.entityToApi(e))
//                .map(e -> {
//                    e.setServiceAddress(serviceUtil.getServiceAddress());
//                    return e;
//                });
//        List<Long> courseIds = enrolmentFlux.toStream().map(Enrolment::getCourseId).collect(Collectors.toList());
//
//        URI url = UriComponentsBuilder.fromUriString(courseServiceUrl + "/course").queryParam("courseIds", courseIds).build().encode().toUri();
//
//        LOG.debug("Will call the getCourse API on URL: {}", url);
//
//        Flux<Course> courseFlux = getWebClient().get().uri(url).headers(h -> h.addAll(headers)).retrieve().bodyToFlux(Course.class).log(null, FINE).onErrorResume(error -> empty());
//        List<Course> courseList = courseFlux.collectList().block();
//        String courseAddress = courseList.get(0).getServiceAddress();
//        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceUtil.getServiceAddress(),courseAddress,null);
//
//        EnrolmentByStudent enrolmentByStudent = new EnrolmentByStudent(studentId, courseList.stream().map(course -> mapper.courseApiToSummary(course)).collect(Collectors.toList()), serviceAddresses);
//
//        return Mono.just(enrolmentByStudent);
    }

    @Override
    public Mono<Course> updateEnrolment(Enrolment body) {
        Course course1 = new Course();
        course1.setCourseId(body.getCourseId());
        course1.setCapacity(40);
        course1.setSpare(15);
        course1.setNumberOfStudents(25);
        course1.setProfessorName("김영한");
        course1.setCredit(3);
        course1.setDepartment(Department.IT_CONVERGENCE);
        course1.setSemester(Semester.FALL);
        course1.setTitle("클라우드융합");
        course1.setYear(2022);

        return Mono.just(course1);

//        Integer studentId = body.getStudentId();
//        Long courseId = body.getCourseId();
//
//        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);
//
//        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);
//
//        LOG.info("Will Enrolment info for student id={}, course id = {}", studentId, courseId);
//
//        if(repository.findByStudentIdAndCourseId(studentId, courseId).hasElement().block())
//            throw new BadRequestException("Already enrolment course, courseId = " + courseId);
//
//        Iterable<EnrolmentEntity> enrolmentEntities = repository.findAllByCourseIdAndStudentIdIsNull(courseId)
//                .switchIfEmpty(Flux.error(new BadRequestException("It's full of Students for course id = " + courseId)))
//                .toIterable();
//        Iterator<EnrolmentEntity> iterator = enrolmentEntities.iterator();
//        while (iterator.hasNext()){
//            try {
//                EnrolmentEntity next = iterator.next();
//                next.setStudentId(studentId);
//                repository.save(next).log(null, FINE);
//
//                Long numberOfStudents = repository.findAllByCourseIdAndStudentIdIsNotNull(courseId).count().block();
//                Course update = new Course(courseId, numberOfStudents == null ? null : numberOfStudents.intValue());
//                messageSources.outputCourses().send(MessageBuilder.withPayload(new Event<>(Event.Type.UPDATE, update.getCourseId(), update)).build());
//
//                URI url = UriComponentsBuilder.fromUriString(courseServiceUrl + "/course/{courseId}").build(courseId);
//                LOG.debug("Will call the getCourse API on URL: {}", url);
//                Mono<Course> course = getWebClient().get().uri(url).retrieve().bodyToMono(Course.class).log(null, FINE).onErrorResume(error -> Mono.empty());
//                return course;
//
//            }catch (OptimisticLockingFailureException o){
//                LOG.info("enrolment fail try again... student id = {}, course id = {}", studentId, courseId);
//            }
//        }
//        throw new BadRequestException("It's full of Students for course id = " + courseId);
    }

    @Override
    public Mono<Void> deleteEnrolment(Enrolment body) {
        return Mono.empty();
//        Integer studentId = body.getStudentId();
//        Long courseId = body.getCourseId();
//
//        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);
//
//        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);
//
//        LOG.info("Will delete Enrolment info for student id={}, course id = {}", studentId, courseId);
//
//         return repository.findByStudentIdAndCourseId(studentId, courseId).log(null, FINE)
//                .map(e -> {
//                    e.setStudentId(null);
//                    return repository.save(e);
//                }).then();
    }

    private void simulateDelay(int delay) {
        LOG.debug("Sleeping for {} seconds...", delay);
        try {Thread.sleep(delay * 1000);} catch (InterruptedException e) {}
        LOG.debug("Moving on...");
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        int randomThreshold = getRandomNumber(1, 100);
        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.warn("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }
    }

    private final Random randomNumberGenerator = new Random();
    private int getRandomNumber(int min, int max) {

        if (max < min) {
            throw new RuntimeException("Max must be greater than min");
        }

        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }
}
