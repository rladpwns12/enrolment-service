package yejun.microservices.core.course.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import yejun.api.course.Course;
import yejun.api.course.CourseService;
import yejun.api.enrolment.EnrolmentService;
import yejun.api.event.Event;
import yejun.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final CourseService courseService;

    @Autowired
    public MessageProcessor(CourseService courseService) {
        this.courseService = courseService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Long, Course> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case UPDATE:
            Course course = event.getData();
            Long courseId = course.getCourseId();

            LOG.info("Update course by enrolment with ID: {}", courseId);

            courseService.updateCourseByEnrolment(course);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a UPDATE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
