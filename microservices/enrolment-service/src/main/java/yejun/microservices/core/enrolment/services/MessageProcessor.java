package yejun.microservices.core.enrolment.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import yejun.api.course.Course;
import yejun.api.enrolment.EnrolmentService;
import yejun.api.event.Event;
import yejun.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final EnrolmentService enrolmentService;

    @Autowired
    public MessageProcessor(EnrolmentService enrolmentService) {
        this.enrolmentService = enrolmentService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Long, Course> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            Course course = event.getData();
            Long courseId = course.getCourseId();
            int capacity = course.getCapacity();

            LOG.info("Create {} of products with ID: {}", capacity, courseId);

            for(int i=0; i<capacity; i++){
                enrolmentService.createEnrolment(courseId);
            }
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
