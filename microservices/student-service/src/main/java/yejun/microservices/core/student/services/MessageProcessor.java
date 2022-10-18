package yejun.microservices.core.student.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import yejun.api.course.Course;
import yejun.api.event.Event;
import yejun.api.student.Student;
import yejun.api.student.StudentService;
import yejun.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final StudentService studentService;

    @Autowired
    public MessageProcessor(StudentService studentService) {
        this.studentService = studentService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Long, Student> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {


            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }
//        LOG.info("Message processing done!");
    }
}
