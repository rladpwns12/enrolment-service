package yejun.api.enrolment;

import io.swagger.annotations.ApiModelProperty;
import yejun.api.common.ServiceAddresses;
import yejun.api.course.Course;

import java.util.List;

public class EnrolmentByStudent {
    private Integer studentId;
    private List<CourseSummary> courses;
    @ApiModelProperty(hidden = true)
    private ServiceAddresses serviceAddresses;

    public EnrolmentByStudent() {
        studentId = null;
        courses = null;
        serviceAddresses = null;
    }

    public EnrolmentByStudent(Integer studentId, List<CourseSummary> courses, ServiceAddresses serviceAddresses) {
        this.studentId = studentId;
        this.courses = courses;
        this.serviceAddresses = serviceAddresses;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public List<CourseSummary> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseSummary> courses) {
        this.courses = courses;
    }

    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }

    public void setServiceAddresses(ServiceAddresses serviceAddresses) {
        this.serviceAddresses = serviceAddresses;
    }
}
