package yejun.microservices.core.course.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.support.ReactiveQuerydslMongoPredicateExecutor;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yejun.api.common.Semester;

import java.util.List;

public interface CourseRepository extends ReactiveCrudRepository<CourseEntity, String> {
    Mono<CourseEntity> findByCourseId(Long courseId);

    Flux<CourseEntity> findAllByStudentId(Long studentId);
    Flux<CourseEntity> findAllByDepartmentAndYearAndSemester(String keyword, int year, Semester semester, Pageable pageable);
    Flux<CourseEntity> findAllByTitleContainingAndYearAndSemester(String keyword, int year, Semester semester, Pageable pageable);
    Flux<CourseEntity> findAllByProfessorNameAndYearAndSemester(String keyword, int year, Semester semester, Pageable pageable);
    Flux<CourseEntity> findAllByCourseIdIn(List<Long> courseIds);
}
