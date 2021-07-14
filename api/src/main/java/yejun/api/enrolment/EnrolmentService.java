package yejun.api.enrolment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yejun.api.course.Course;

@Api("REST API for Enrolment information.")
public interface EnrolmentService {
    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/enrolment \
     *   -H "Content-Type: application/json" --data \
     *   '{"courseId":"2150685201"}'
     *
     * @param courseId
     */
    @ApiOperation(
            value = "${api.enrolment.create-enrolment.description}",
            notes = "${api.enrolment.create-enrolment.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PostMapping(
            value    = "/enrolment",
            consumes = "application/json")
    Mono<Enrolment> createEnrolment(@RequestBody Long courseId);

    /**
     * Sample usage: curl -X GET $HOST:$PORT/enrolment?studentId=20142058&year=2021&semester=FALL&page=0&size=20
     * -H "accept: * / *"
     *
     * @param enrolmentRequestDTO
     * @return the enrolment info, if found, else null page
     */
    @ApiOperation(
            value = "${api.enrolment.get-enrolment.description}",
            notes = "${api.enrolment.get-enrolment.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value    = "/enrolment",
            produces = "application/json")
    Flux<Course> getEnrolment(
            @RequestHeader HttpHeaders headers,
            @PathVariable EnrolmentRequestDTO enrolmentRequestDTO,
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    );

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/enrolment \
     *   -H "Content-Type: application/json" --data \
     *   '{"studentId":20142058, "courseId":"2150685201"}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.enrolment.update-enrolment.description}",
            notes = "${api.enrolment.update-enrolment.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PutMapping(
            value    = "/enrolment",
            consumes = "application/json")
    Mono<Course> updateEnrolment(@RequestBody Enrolment body);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/enrolment \
     *   -H "Content-Type: application/json" --data \
     *   '{"studentId":20142058, "courseId":"2150685201"}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.enrolment.delete-enrolment.description}",
            notes = "${api.enrolment.delete-enrolment.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @DeleteMapping(
            value    = "/enrolment",
            consumes = "application/json")
    Mono<Void> deleteEnrolment(@RequestBody Enrolment body);
}
