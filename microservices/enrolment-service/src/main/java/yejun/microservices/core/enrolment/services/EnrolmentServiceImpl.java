package yejun.microservices.core.enrolment.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yejun.api.course.Course;
import yejun.api.enrolment.Enrolment;
import yejun.api.enrolment.EnrolmentRequestDTO;
import yejun.api.enrolment.EnrolmentService;
import yejun.util.http.ServiceUtil;

@RestController
public class EnrolmentServiceImpl implements EnrolmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EnrolmentServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public EnrolmentServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Enrolment> createEnrolment(Long courseId) {
        return null;
    }

    @Override
    public Flux<Course> getEnrolment(HttpHeaders headers, EnrolmentRequestDTO enrolmentRequestDTO, int delay, int faultPercent) {
        return null;
    }

    @Override
    public Mono<Course> updateEnrolment(Enrolment body) {
        return null;
    }

    @Override
    public Mono<Void> deleteEnrolment(Enrolment body) {
        return null;
    }

}
