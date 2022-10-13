package yejun.microservices.core.course.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yejun.api.common.Department;
import yejun.api.common.MessageSources;
import yejun.api.common.Semester;
import yejun.api.common.Type;
import yejun.api.course.Course;
import yejun.api.course.CourseRequestDTO;
import yejun.api.course.CourseService;
import yejun.api.event.Event;
import yejun.microservices.core.course.persistence.CourseEntity;
import yejun.microservices.core.course.persistence.CourseRepository;
import yejun.util.exceptions.InvalidInputException;
import yejun.util.exceptions.NotFoundException;
import yejun.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Mono.error;


@RestController
@EnableBinding(MessageSources.class)
@CrossOrigin
public class CourseServiceImpl implements CourseService {

    private final ServiceUtil serviceUtil;

    private final CourseRepository repository;

    private final CourseMapper mapper;

    private final MessageSources messageSources;

    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    public CourseServiceImpl(ServiceUtil serviceUtil, CourseRepository repository, CourseMapper mapper, MessageSources messageSources) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
        this.messageSources = messageSources;
    }



    @Override
    public Mono<Course> createCourse(Course body) {
        body.setCourseId(2150685201L);
        body.setSpare(body.getCapacity());
        body.setNumberOfStudents(0);
        body.setProfessorName("김영한");
        return Mono.just(body);
//        if (body.getCourseId() < 1) throw new InvalidInputException("Invalid courseId: " + body.getCourseId());
//
//        body.setSpare(body.getCapacity());
//        body.setNumberOfStudents(0);
//
//        CourseEntity entity = mapper.apiToEntity(body);
//
//        Mono<Course> newEntity = repository.save(entity)
//                .log(null, FINE)
//                .onErrorMap(
//                        DuplicateKeyException.class,
//                        ex -> new InvalidInputException("Duplicate key, Course Id: " + body.getCourseId()))
//                .map(mapper::entityToApi);
//
//        messageSources.outputEnrolments().send(MessageBuilder.withPayload(new Event<>(Event.Type.CREATE, body.getCourseId(), body)).build());
//
//        return newEntity;
    }

    @Override
    public Mono<Course> getCourse(HttpHeaders headers, Long courseId) {
        Course course = new Course();
        course.setCourseId(courseId);
        course.setCapacity(40);
        course.setSpare(15);
        course.setNumberOfStudents(25);
        course.setProfessorName("김영한");
        course.setCredit(3);
        course.setDepartment(Department.IT_CONVERGENCE);
        course.setSemester(Semester.FALL);
        course.setTitle("클라우드융합");
        course.setYear(2022);

        return Mono.just(course);
//        if (courseId <1) throw new InvalidInputException("Invalid courseId: " + courseId);
//
//        LOG.info("Will get course info for id={}", courseId);
//
//        return repository.findByCourseId(courseId)
//                .switchIfEmpty(error(new NotFoundException("No Course found for courseId: " + courseId)))
//                .log(null, FINE)
//                .map(mapper::entityToApi)
//                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public Flux<Course> getCourse(List<Long> courseIds) {
        LOG.info("Will get courses info for ids = {}", courseIds);

        return repository.findAllByCourseIdIn(courseIds)
                .log(null, FINE)
                .map(e -> mapper.entityToApi(e))
                .map(e ->{e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public Flux<Course> getCourses(HttpHeaders headers, CourseRequestDTO courseRequestDTO) {
        List<Course> courseList = new ArrayList<>();
        Course course1 = new Course();
        course1.setCourseId(2150685201L);
        course1.setCapacity(40);
        course1.setSpare(15);
        course1.setNumberOfStudents(25);
        course1.setProfessorName("김영한");
        course1.setCredit(3);
        course1.setDepartment(Department.IT_CONVERGENCE);
        course1.setSemester(courseRequestDTO.getSemester());
        course1.setTitle("클라우드융합");
        course1.setYear(courseRequestDTO.getYear());
        courseList.add(course1);

        Course course2 = new Course();
        course2.setCourseId(25685201L);
        course2.setCapacity(50);
        course2.setSpare(1);
        course2.setNumberOfStudents(49);
        course2.setProfessorName("Anjolinya Jolyeo");
        course2.setCredit(4);
        course2.setDepartment(Department.SCHOOL_OF_BUSINESS_ADMINISTRATION);
        course2.setSemester(courseRequestDTO.getSemester());
        course2.setTitle("Zip e ga go ship da");
        course2.setYear(courseRequestDTO.getYear());
        courseList.add(course2);
        return Flux.just(courseList.toArray(new Course[courseList.size()]));
//        LOG.info("Will get courses info for courseRequestDTO={}", courseRequestDTO.toString());
//
//        Pageable pageable = PageRequest.of(courseRequestDTO.getPage(), courseRequestDTO.getSize());
//
//        return getCoursesByType(courseRequestDTO.getType(), courseRequestDTO.getKeyword(), courseRequestDTO.getYear(), courseRequestDTO.getSemester(), pageable);
    }

    private Flux<Course> getCoursesByType(Type type, String keyword, int year, Semester semester, Pageable pageable){
        switch (type){
            case DEPARTMENT:
                repository.findAllByDepartmentAndYearAndSemester(keyword, year, semester, pageable)
                        .log(null, FINE)
                        .map(mapper::entityToApi)
                        .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
            case PROFESSOR:
                repository.findAllByProfessorNameAndYearAndSemester(keyword, year, semester, pageable)
                        .log(null, FINE)
                        .map(mapper::entityToApi)
                        .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
            case TITLE:
                repository.findAllByTitleAndYearAndSemester(keyword, year, semester, pageable)
                        .log(null, FINE)
                        .map(mapper::entityToApi)
                        .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
            default:
                throw new InvalidInputException("Invalid Type: " + type);
        }
    }

    @Override
    public Mono<Course> updateCourse(Course body) {
        Long courseId = body.getCourseId();

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        final int[] updateCapacity = {0};

        Mono<Course> updateEntity = repository.findByCourseId(courseId)
                .switchIfEmpty(error(new NotFoundException("No course found for courseId: " + courseId)))
                .map(e -> {
                    updateCapacity[0] = ((body.getCapacity() == null) ? 0 : e.getCapacity() - body.getCapacity());

                    if (updateCapacity[0] < 0)
                        throw new InvalidInputException("Can't reduce capacity.");
                    else {
                        e.setSpare(e.getSpare() + updateCapacity[0]);
                        return repository.save(mapper.updateEntity(body, e));
                    }
                }).flatMap(e -> e).map(mapper::entityToApi);

        if (updateCapacity[0] > 0) {
            body.setCapacity(updateCapacity[0]);
            messageSources.outputEnrolments().send(MessageBuilder.withPayload(new Event<>(Event.Type.CREATE, body.getCourseId(), body)).build());
        }
        return updateEntity;
    }

    @Override
    public void updateCourseByEnrolment(Course body) {
        Long courseId = body.getCourseId();

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        repository.findByCourseId(courseId)
                .switchIfEmpty(error(new NotFoundException("No course found for courseId: " + courseId)))
                .map(e -> repository.save(mapper.updateEntityByEnrolment(body, e)));
        return;
    }

    @Override
    public Mono<Void> deleteCourse(Long courseId) {
        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.debug("deleteCourse: tries to delete an entity with courseId: {}", courseId);
        return repository.findByCourseId(courseId).log(null, FINE).map(repository::delete).flatMap(e -> e);
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
