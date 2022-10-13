package yejun.microservices.core.course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import yejun.microservices.core.course.persistence.CourseEntity;

import static java.util.Collections.emptyList;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@SpringBootApplication
@ComponentScan("yejun")
public class CourseServiceApplication {

	private static final Logger LOG = LoggerFactory.getLogger(CourseServiceApplication.class);

	@Value("${api.course.common.version}")           String apiVersion;
	@Value("${api.course.common.title}")             String apiTitle;
	@Value("${api.course.common.description}")       String apiDescription;
	@Value("${api.course.common.termsOfServiceUrl}") String apiTermsOfServiceUrl;
	@Value("${api.course.common.license}")           String apiLicense;
	@Value("${api.course.common.licenseUrl}")        String apiLicenseUrl;
	@Value("${api.course.common.contact.name}")      String apiContactName;
	@Value("${api.course.common.contact.url}")       String apiContactUrl;
	@Value("${api.course.common.contact.email}")     String apiContactEmail;

	/**
	 * Will exposed on $HOST:$PORT/swagger-ui.html
	 *
	 * @return
	 */
	@Bean
	public Docket apiDocumentation() {

		return new Docket(SWAGGER_2)
				.select()
				.apis(basePackage("yejun.microservices.core.course"))
				.paths(PathSelectors.any())
				.build()
				.useDefaultResponseMessages(true)
				.apiInfo(new ApiInfo(
						apiTitle,
						apiDescription,
						apiVersion,
						apiTermsOfServiceUrl,
						new Contact(apiContactName, apiContactUrl, apiContactEmail),
						apiLicense,
						apiLicenseUrl,
						emptyList()
				));
	}

//	@Bean
//	@LoadBalanced
//	public WebClient.Builder loadBalancedWebClientBuilder() {
//		final WebClient.Builder builder = WebClient.builder();
//		return builder;
//	}

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = SpringApplication.run(CourseServiceApplication.class, args);

		String mongodDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
		String mongodDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
		LOG.info("Connected to MongoDb: " + mongodDbHost + ":" + mongodDbPort);
	}

	@Autowired
	ReactiveMongoOperations mongoTemplate;

	@EventListener(ContextRefreshedEvent.class)
	public void initIndicesAfterStartup() {

		MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
		IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

		ReactiveIndexOperations indexOps = mongoTemplate.indexOps(CourseEntity.class);
		resolver.resolveIndexFor(CourseEntity.class).forEach(e -> indexOps.ensureIndex(e).block());
	}

}
