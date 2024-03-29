package yejun.api.student;

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

@Api("REST API for student information.")
public interface StudentService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/student \
     *   -H "Content-Type: application/json" --data \
     *   '{"email":rladpwns12@gmail.com,"name":"김예준","password":qwer1234, "department":"IT_CONVERGENCE"}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.student.create-student.description}",
            notes = "${api.student.create-student.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PostMapping(
            value    = "/student",
            consumes = "application/json")
    Mono<Student> createStudent(@RequestBody Student body);

    /**
     * Sample usage: curl $HOST:$PORT/student/20142058
     *
     * @param studentId
     * @return the student info, if found, else null
     */
    @ApiOperation(
            value = "${api.student.get-student.description}",
            notes = "${api.student.get-student.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value    = "/student/{studentId}",
            produces = "application/json")
    Mono<Student> getStudent(
            @RequestHeader HttpHeaders headers,
            @PathVariable Integer studentId
    );

    /**
     * Sample usage: curl $HOST:$PORT/student?studentIds=20142058&studentIds=20142345
     *
     * @param studentIds
     * @return the student info, if found, else null
     */
    @ApiOperation(
            value = "${api.student.get-students.description}",
            notes = "${api.student.get-students.notes}",
            hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value    = "/student",
            produces = "application/json")
    Flux<Student> getStudent(List<Integer> studentIds);

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/student \
     *   -H "Content-Type: application/json" --data \
     *   '{"email":rladpwns12@gmail.com,"name":"김예준","password":qwer1234, "department":"SCHOOL_OF_COMPUTING"}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.student.update-student.description}",
            notes = "${api.student.update-student.notes}",
            hidden = true
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PutMapping(
            value    = "/student",
            consumes = "application/json")
    Mono<Student> updateStudent(@RequestBody Student body);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/student/20142058
     *
     * @param studentId
     */
    @ApiOperation(
            value = "${api.student.delete-student.description}",
            notes = "${api.student.delete-student.notes}",
            hidden = true
    )
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @DeleteMapping(value = "/student/{studentId}")
    Mono<Void> deleteStudent(@PathVariable Integer studentId);

    @GetMapping("/student/api")
    @ApiIgnore
    void api(HttpServletResponse response) throws IOException;
}
