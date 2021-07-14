package yejun.api.enrolment;

public class Enrolment {
    private int studentId;
    private String courseId;

    public Enrolment(){
        studentId = 0;
        courseId = null;
    }

    public Enrolment(int studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
