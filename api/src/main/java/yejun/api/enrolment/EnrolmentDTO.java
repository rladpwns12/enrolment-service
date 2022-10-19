package yejun.api.enrolment;

public class EnrolmentDTO {
    Long courseId;

    public EnrolmentDTO() {
        courseId = null;
    }

    public EnrolmentDTO(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

}
