
package yejun.api.course;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api("REST API for course information.")
public interface CourseService {

    /**
     * Sample usage:
     * <p>
     * curl -X POST $HOST:$PORT/course \
     * -H "Content-Type: application/json" --data \
     * '{"title":"클라우드융합", "studentId":"11123442", "credit":3, "capacity":40, "department":"IT_CONVERGENCE"}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.course.create-course.description}",
            notes = "${api.course.create-course.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PostMapping(
            value = "/course",
            consumes = "application/json")
    Mono<Course> createCourse(@RequestBody Course body);

    /**
     * Sample usage: curl $HOST:$PORT/course/2150685201
     *
     * @param courseId
     * @return the course info, if found, else null
     */
    @ApiOperation(
            value = "${api.course.get-course.description}",
            notes = "${api.course.get-course.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value = "/course/{courseId}",
            produces = "application/json")
    Mono<Course> getCourse(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long courseId
    );

    /**
     * Sample usage: curl $HOST:$PORT/course?courseIds=2150685201&courseIds=2150685202
     *
     * @param courseIds
     * @return the course info, if found, else null
     */
    @ApiOperation(
            value = "${api.course.get-course.description}",
            notes = "${api.course.get-course.notes}",
            hidden = true
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value = "/course",
            produces = "application/json")
    Flux<Course> getCourse(@RequestParam List<Long> courseIds);

    /**
     * Sample usage: curl -X GET $HOST:$PORT/courses?type=PROFESSOR&keyword=김영한&year=2021&semester=FALL&page=0&size=20
     * -H "accept: * /*"
     *
     * @param courseRequestDTO
     * @return the course info, if found, else null page
     */
    @ApiOperation(
            value = "${api.course.get-courses.description}",
            notes = "${api.course.get-courses.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value = "/course/courses",
            produces = "application/json")
    Flux<Course> getCourses(
            @RequestHeader HttpHeaders headers,
            CourseRequestDTO courseRequestDTO
    );

    @GetMapping(
            value = "/course/professor/{professorId}",
            produces = "application/json")
    Flux<Course> getCourseByProfessor(
            @RequestHeader HttpHeaders headers,
            @PathVariable Long professorId
    );

    /**
     * Sample usage:
     * <p>
     * curl -X POST $HOST:$PORT/course \
     * -H "Content-Type: application/json" --data \
     * '{"courseId":2150685201, "title":"IT융합응용", "capacity":30 }'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.course.update-course.description}",
            notes = "${api.course.update-course.notes}",
            hidden = true
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PutMapping(
            value = "/course",
            consumes = "application/json")
    Mono<Course> updateCourse(@RequestBody Course body);

    void updateCourseByEnrolment(Course body);

    /**
     * Sample usage:
     * <p>
     * curl -X DELETE $HOST:$PORT/course/2150685201
     *
     * @param courseId
     */
    @ApiOperation(
            value = "${api.course.delete-course.description}",
            notes = "${api.course.delete-course.notes}",
            hidden = true
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @DeleteMapping(value = "/course/{courseId}")
    Mono<Void> deleteCourse(@PathVariable Long courseId);

    @GetMapping("/course/api")
    @ApiIgnore
    void api(HttpServletResponse response) throws IOException;
}
