package yejun.api.enrolment;

import yejun.api.common.ServiceAddresses;

import java.util.List;

public class EnrolmentByCourse {
    private Long courseId;
    private List<StudentSummary> students;
    private ServiceAddresses serviceAddresses;

    public EnrolmentByCourse() {
        courseId = null;
        students = null;
        students = null;
    }

    public EnrolmentByCourse(Long courseId, List<StudentSummary> students, ServiceAddresses serviceAddresses) {
        this.courseId = courseId;
        this.students = students;
        this.serviceAddresses = serviceAddresses;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<StudentSummary> getStudents() {
        return students;
    }

    public void setStudents(List<StudentSummary> students) {
        this.students = students;
    }

    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }

    public void setServiceAddresses(ServiceAddresses serviceAddresses) {
        this.serviceAddresses = serviceAddresses;
    }
}
