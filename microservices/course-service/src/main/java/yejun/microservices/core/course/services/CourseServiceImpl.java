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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

import java.util.Random;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Mono.error;


@RestController
@EnableBinding(MessageSources.class)
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
        if (body.getCourseId() < 1) throw new InvalidInputException("Invalid courseId: " + body.getCourseId());

        body.setSpare(body.getCapacity());
        body.setNumberOfStudents(0);

        CourseEntity entity = mapper.apiToEntity(body);

        Mono<Course> newEntity = repository.save(entity)
                .log(null, FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Course Id: " + body.getCourseId()))
                .map(mapper::entityToApi);

        messageSources.outputEnrolments().send(MessageBuilder.withPayload(new Event(Event.Type.CREATE, body.getCourseId(), body)).build());

        return newEntity;
    }

    @Override
    public Mono<Course> getCourse(HttpHeaders headers, Long courseId, int delay, int faultPercent) {
        if (courseId <1) throw new InvalidInputException("Invalid courseId: " + courseId);

        if (delay > 0) simulateDelay(delay);

        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);

        LOG.info("Will get course info for id={}", courseId);

        return repository.findByCourseId(courseId)
                .switchIfEmpty(error(new NotFoundException("No Course found for courseId: " + courseId)))
                .log(null, FINE)
                .map(mapper::entityToApi)
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public Flux<Course> getCourses(HttpHeaders headers, CourseRequestDTO courseRequestDTO, int delay, int faultPercent) {
        if (delay > 0) simulateDelay(delay);

        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);

        LOG.info("Will get courses info for courseRequestDTO={}", courseRequestDTO.toString());

        Pageable pageable = PageRequest.of(courseRequestDTO.getPage(), courseRequestDTO.getSize());

        return getCoursesByType(courseRequestDTO.getType(), courseRequestDTO.getKeyword(), courseRequestDTO.getYear(), courseRequestDTO.getSemester(), pageable);
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
                    updateCapacity[0] = e.getCapacity() - body.getCapacity();
                    if (updateCapacity[0] < 0)
                        throw new InvalidInputException("Can't reduce capacity.");
                    else {
                        return repository.save(mapper.updateEntity(body, e));
                    }
                }).flatMap(e -> e).map(mapper::entityToApi);

        if (updateCapacity[0] > 0) {
            body.setCapacity(updateCapacity[0]);
            messageSources.outputEnrolments().send(MessageBuilder.withPayload(new Event(Event.Type.CREATE, body.getCourseId(), body)).build());
        }
        return updateEntity;
    }


    @Override
    public Mono<Void> deleteCourse(Long courseId) {
        if (courseId < 1) throw new InvalidInputException("Invalid courseId: " + courseId);

        LOG.debug("deleteCourse: tries to delete an entity with courseId: {}", courseId);
        return repository.findByCourseId(courseId).log(null, FINE).map(e -> repository.delete(e)).flatMap(e -> e);
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
