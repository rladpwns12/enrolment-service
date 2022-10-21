package yejun.microservices.core.enrolment.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jdk.internal.org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.integration.handler.advice.RequestHandlerCircuitBreakerAdvice;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
import yejun.util.exceptions.NotFoundException;
import yejun.util.http.HttpErrorInfo;
import yejun.util.http.ServiceUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
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

    private final int productServiceTimeoutSec;

    private final String studentServiceUrl = "http://student";

    private final String courseServiceUrl = "http://course";

    private final ServiceUtil serviceUtil;

    private final EnrolmentRepository repository;

    private final EnrolmentMapper mapper;

    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    private final MessageSources messageSources;

    private WebClient webClient;


    @Autowired
    public EnrolmentServiceImpl(ServiceUtil serviceUtil, EnrolmentRepository repository, EnrolmentMapper mapper, WebClient.Builder webClientBuilder, MessageSources messageSources, @Value("#{new Integer('${app.product-service.timeoutSec}')}") int productServiceTimeoutSec, ObjectMapper objectMapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
        this.webClientBuilder = webClientBuilder;
        this.messageSources = messageSources;
        this.productServiceTimeoutSec = productServiceTimeoutSec;
        this.objectMapper = objectMapper;
    }

    public WebClient getWebClient() {
        if (webClient == null)
            webClient = webClientBuilder.build();
        return webClient;
    }

    @Override
    public Mono<Enrolment> createEnrolment(EnrolmentDTO enrolmentDTO) {
        Long courseId = enrolmentDTO.getCourseId();
        if (courseId < 1) return Mono.error(new InvalidInputException("Invalid courseId: " + courseId));

        Enrolment enrolment = new Enrolment(null, courseId, null);
        EnrolmentEntity entity = mapper.apiToEntity(enrolment);

        Mono<Enrolment> newEntity = repository.save(entity)
                .log(null, FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Course Id: " + courseId))
                .map(mapper::entityToApi)
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });

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
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceUtil.getServiceAddress(), null, studentAddress);

        EnrolmentByCourse enrolmentByCourse = new EnrolmentByCourse(courseId, studentList.stream().map(student -> mapper.studentApiToSummary(student)).collect(Collectors.toList()), serviceAddresses);

        return Mono.just(enrolmentByCourse);
    }

    @Override
    public Mono<EnrolmentByStudent> getEnrolmentByStudent(HttpHeaders headers, EnrolmentStudentDTO enrolmentStudentDTO) {
        Integer studentId = enrolmentStudentDTO.getStudentId();
        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);

        LOG.info("Will get Enrolment info for student id={}", studentId);

        Flux<Enrolment> enrolmentFlux = repository.findAllByStudentId(studentId)
                .log(null, FINE)
                .map(e -> mapper.entityToApi(e))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });

        List<Long> courseIds = enrolmentFlux.toStream().map(Enrolment::getCourseId).collect(Collectors.toList());
        return Mono.zip(value -> createEnrolmentByStudent((List<Course>) value[0], enrolmentStudentDTO),
                        getCourseAboutEnrolment(headers, courseIds).collectList())
                                .onErrorReturn(CallNotPermittedException.class,
                                        getEnrolmentByStudentFallbackValue(headers, enrolmentStudentDTO))
                .doOnError(ex -> LOG.warn("getEnrolmentByStudent failed: {}", ex.toString()))
                .log(null, FINE);

    }

    private EnrolmentByStudent getEnrolmentByStudentFallbackValue(HttpHeaders headers, EnrolmentStudentDTO enrolmentStudentDTO) {
        LOG.info("getEnrolmentByStudentFallbackValue!!.. about : {}", enrolmentStudentDTO.getStudentId());
        return new EnrolmentByStudent(enrolmentStudentDTO.getStudentId(), null, null);
    }

    private EnrolmentByStudent createEnrolmentByStudent(List<Course> courseList, EnrolmentStudentDTO enrolmentStudentDTO) {
        ServiceAddresses serviceAddresses = new ServiceAddresses();
        if (!courseList.isEmpty()) {
            String courseAddress = courseList.get(0).getServiceAddress();
            serviceAddresses = new ServiceAddresses(serviceUtil.getServiceAddress(), courseAddress, null);
        }
        List<CourseSummary> collect = courseList.stream().map(course -> mapper.courseApiToSummary(course))
                .filter(courseSummary -> courseSummary.getYear() == enrolmentStudentDTO.getYear())
                .filter(courseSummary -> courseSummary.getSemester() == enrolmentStudentDTO.getSemester())
                .collect(Collectors.toList());
        return new EnrolmentByStudent(enrolmentStudentDTO.getStudentId(), collect, serviceAddresses);
    }

    @CircuitBreaker(name = "getCourseAboutEnrolment")
    public Flux<Course> getCourseAboutEnrolment(HttpHeaders headers, List<Long> courseIds) {
        URI url = UriComponentsBuilder.fromUriString(courseServiceUrl + "/course").queryParam("courseIds", courseIds).build().encode().toUri();
        LOG.debug("Will call the getCourse API on URL: {}", url);
        return getWebClient().get().uri(url).headers(h -> h.addAll(headers)).retrieve().bodyToFlux(Course.class)
                .log(null, FINE)
                .onErrorMap(WebClientResponseException.class, e -> handleException(e))
                .timeout(Duration.ofSeconds(productServiceTimeoutSec));
    }

    @Override
    @Retry(name = "enrolment")
    public Mono<Void> updateEnrolment(Enrolment body) {
        Integer studentId = body.getStudentId();
        Long courseId = body.getCourseId();

        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.info("Will Enrolment info for student id={}, course id = {}", studentId, courseId);

        if (repository.findByStudentIdAndCourseId(studentId, courseId).hasElement().block())
            throw new BadRequestException("Already enrolment course, courseId = " + courseId);

        Iterable<EnrolmentEntity> enrolmentEntities = repository.findAllByCourseIdAndStudentIdIsNull(courseId)
                .switchIfEmpty(Flux.error(new BadRequestException("It's full of Students for course id = " + courseId)))
                .toIterable();
        Iterator<EnrolmentEntity> iterator = enrolmentEntities.iterator();
        while (iterator.hasNext()) {
            try {
                EnrolmentEntity next = iterator.next();
                next.setStudentId(studentId);
                return repository.save(next).doOnSuccess(enrolmentEntity -> {
                    Course update = new Course(courseId);
                    messageSources.outputEnrolments().send(MessageBuilder.withPayload(new Event<>(Event.Type.UPDATE, update.getCourseId(), update)).build());
                }).timeout(Duration.ofSeconds(productServiceTimeoutSec)).then();
            } catch (OptimisticLockingFailureException o) {
                LOG.info("enrolment fail try again... student id = {}, course id = {}", studentId, courseId);
                continue;
            }
        }
        throw new BadRequestException("It's full of Students for course id = " + courseId);
    }

    public void updateCourseSpare(Course course) {
        Long courseId = course.getCourseId();
        Long numberOfStudents = repository.findAllByCourseIdAndStudentIdIsNotNull(courseId).count().block();
        LOG.info(" courseId : {} about numberOfStudents is {}...", courseId, numberOfStudents);
        course.setNumberOfStudents(numberOfStudents == null ? null : numberOfStudents.intValue());
        messageSources.outputCourses().send(MessageBuilder.withPayload(new Event<>(Event.Type.UPDATE, course.getCourseId(), course)).build());
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
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
        }
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

    @Override
    public void api(HttpServletResponse response) throws IOException {
        String redirect_uri = "/swagger-ui/index.html";
        response.sendRedirect(redirect_uri);
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException) ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}
