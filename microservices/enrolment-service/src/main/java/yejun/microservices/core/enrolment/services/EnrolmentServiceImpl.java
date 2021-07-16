package yejun.microservices.core.enrolment.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yejun.api.course.Course;
import yejun.api.enrolment.*;
import yejun.microservices.core.enrolment.persistence.EnrolmentEntity;
import yejun.microservices.core.enrolment.persistence.EnrolmentRepository;
import yejun.util.exceptions.BadRequestException;
import yejun.util.exceptions.InvalidInputException;
import yejun.util.http.ServiceUtil;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.logging.Level.FINE;

@RestController
public class EnrolmentServiceImpl implements EnrolmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EnrolmentServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final EnrolmentRepository repository;

    private final EnrolmentMapper mapper;


    @Autowired
    public EnrolmentServiceImpl(ServiceUtil serviceUtil, EnrolmentRepository repository, EnrolmentMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
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
        //TODO studentIds 를 이용해 StudentService와 event 메세지 방식 통신을 통해 Student정보 가져오기

        return null;
    }

    @Override
    public Mono<EnrolmentByStudent> getEnrolmentByStudent(HttpHeaders headers, EnrolmentStudentDTO enrolmentStudentDTO, int delay, int faultPercent) {
        Integer studentId = enrolmentStudentDTO.getStudentId();
        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);

        if (delay > 0) simulateDelay(delay);

        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);

        LOG.info("Will get Enrolment info for student id={}", studentId);

        Flux<Enrolment> enrolmentFlux = repository.findAllByStudentId(studentId)
                .log(null, FINE)
                .map(e -> mapper.entityToApi(e))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
        List<Long> courseIds = enrolmentFlux.toStream().map(Enrolment::getCourseId).collect(Collectors.toList());
        //TODO courseIds 를 이용해 CourseService와 event 메세지 방식 통신을 통해 Course 정보 가져오기

        return null;
    }

    @Override
    public Mono<Course> updateEnrolment(Enrolment body) {
        Integer studentId = body.getStudentId();
        Long courseId = body.getCourseId();

        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.info("Will Enrolment info for student id={}, course id = {}", studentId, courseId);

        repository.findByStudentId(studentId)
                .map(e ->{
                    if(e.getStudentId().equals(studentId))
                        throw new BadRequestException("Already enrolment course, courseId = " + courseId);
                    return e;
                });

        repository.findAllByCourseIdAndStudentIdIsNull(courseId)
                .switchIfEmpty(Flux.error(new BadRequestException("It's full of Students for course id = " + courseId)))
                .toIterable().forEach(e ->{
                    try{
                        e.setStudentId(studentId);
                        repository.save(e)
                        .log(null, FINE);
                        //TODO courseId에 해당하는 Course를 CourseService와 Event 메세지 통신 하여 가져오기
                        return;
                    }catch (OptimisticLockingFailureException o){
                        LOG.info("enrolment fail try again... student id = {}, course id = {}", studentId, courseId);
                    }
                });
        throw new BadRequestException("It's full of Students for course id = " + courseId);
    }

    @Override
    public Mono<Void> deleteEnrolment(Enrolment body) {
        Integer studentId = body.getStudentId();
        Long courseId = body.getCourseId();

        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);

        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.info("Will delete Enrolment info for student id={}, course id = {}", studentId, courseId);

         return repository.findByStudentIdAndCourseId(studentId, courseId).log(null, FINE)
                .map(e -> {
                    e.setStudentId(null);
                    return repository.save(e);
                }).then();
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
