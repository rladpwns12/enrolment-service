package yejun.microservices.core.course.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yejun.api.course.Course;
import yejun.api.course.CourseRequestDTO;
import yejun.api.course.CourseService;
import yejun.util.http.ServiceUtil;


@RestController
public class CourseServiceImpl implements CourseService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public CourseServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Course> createCourse(Course body) {
        return null;
    }

    @Override
    public Mono<Course> getCourse(HttpHeaders headers, Long courseId, int delay, int faultPercent) {
        return null;
    }

    @Override
    public Flux<Course> getCourses(HttpHeaders headers, CourseRequestDTO courseRequestDTO, int delay, int faultPercent) {
        return null;
    }

    @Override
    public Mono<Course> updateCourse(Course body) {
        return null;
    }

    @Override
    public Mono<Void> deleteCourse(Long courseId) {
        return null;
    }

}
