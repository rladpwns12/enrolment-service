package yejun.api.enrolment;

public class Enrolment {
    private Integer studentId;
    private Long courseId;
    private String serviceAddress;

    public Enrolment(){
        studentId = null;
        courseId = null;
        serviceAddress = null;
    }

    public Enrolment(Integer studentId, Long courseId, String serviceAddress) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.serviceAddress = serviceAddress;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
