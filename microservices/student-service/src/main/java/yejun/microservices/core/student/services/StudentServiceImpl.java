package yejun.microservices.core.student.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import yejun.api.student.Student;
import yejun.api.student.StudentService;
import yejun.microservices.core.student.persistence.StudentEntity;
import yejun.microservices.core.student.persistence.StudentRepository;
import yejun.util.exceptions.InvalidInputException;
import yejun.util.exceptions.NotFoundException;
import yejun.util.http.ServiceUtil;

import java.util.Random;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Mono.error;

@RestController
public class StudentServiceImpl implements StudentService {

    private static final Logger LOG = LoggerFactory.getLogger(StudentServiceImpl.class);

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final ServiceUtil serviceUtil;

    private final StudentRepository repository;

    private final StudentMapper mapper;

    @Autowired
    public StudentServiceImpl(StudentRepository repository, StudentMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Student> createStudent(Student body) {
        if (body.getStudentId() < 1) throw new InvalidInputException("Invalid studentId: " + body.getStudentId());

        String hash = encoder.encode(body.getPassword());
        body.setPassword(hash);
        StudentEntity entity = mapper.apiToEntity(body);

        Mono<Student> newEntity = repository.save(entity)
                .log(null, FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Student Id: " + body.getStudentId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity;
    }

    @Override
    public Mono<Student> getStudent(HttpHeaders headers, int studentId, int delay, int faultPercent) {
        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);

        if (delay > 0) simulateDelay(delay);

        if (faultPercent > 0) throwErrorIfBadLuck(faultPercent);

        LOG.info("Will get student info for id={}", studentId);

        return repository.findByStudentId(studentId)
                .switchIfEmpty(error(new NotFoundException("No student found for studentId: " + studentId)))
                .log(null, FINE)
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public Mono<Student> updateStudent(Student body) {
        int studentId = body.getStudentId();
        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);
        return repository.findByStudentId(studentId).switchIfEmpty(error(new NotFoundException("No student found for studentId: " + studentId)))
                .map(e -> repository.save(mapper.updateEntity(body, e))).flatMap(e -> e).map(e -> mapper.entityToApi(e));
    }

    @Override
    public Mono<Void> deleteStudent(int studentId) {

        if (studentId < 1) throw new InvalidInputException("Invalid studentId: " + studentId);

        LOG.debug("deleteStudent: tries to delete an entity with studentId: {}", studentId);
        return repository.findByStudentId(studentId).log(null, FINE).map(e -> repository.delete(e)).flatMap(e -> e);
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