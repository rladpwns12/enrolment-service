package yejun.microservices.core.enrolment.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EnrolmentRepository extends ReactiveCrudRepository<EnrolmentEntity, String> {
    Mono<EnrolmentEntity> findByStudentId(Integer studentId);
    Mono<EnrolmentEntity> findByStudentIdAndCourseId(Integer studentId, Long courseId);
    Flux<EnrolmentEntity> findAllByCourseIdAndStudentIdIsNull(Long courseId);
    Flux<EnrolmentEntity> findAllByCourseIdAndStudentIdIsNotNull(Long courseId);
    Flux<EnrolmentEntity> findAllByStudentId(Integer studentId);
}
