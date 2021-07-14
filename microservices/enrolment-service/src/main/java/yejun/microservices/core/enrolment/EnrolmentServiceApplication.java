package yejun.microservices.core.enrolment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("yejun")
public class EnrolmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnrolmentServiceApplication.class, args);
	}
}
