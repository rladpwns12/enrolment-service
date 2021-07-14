package yejun.microservices.core.student.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StudentRepository extends ReactiveCrudRepository<StudentEntity, String> {
    Mono<StudentEntity> findByStudentId(int studentId);

}
