package yejun.api.common;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MessageSources {

    String OUTPUT_ENROLMENTS = "output-enrolments";

    @Output(OUTPUT_ENROLMENTS)
    MessageChannel outputEnrolments();
}
