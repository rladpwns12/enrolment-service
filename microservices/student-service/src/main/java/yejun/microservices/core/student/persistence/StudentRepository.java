package yejun.microservices.core.student.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StudentRepository extends ReactiveCrudRepository<StudentEntity, String> {
    Mono<StudentEntity> findByStudentId(Integer studentId);
    Flux<StudentEntity> findAllByStudentIdIn(List<Integer> studentIds);
}
