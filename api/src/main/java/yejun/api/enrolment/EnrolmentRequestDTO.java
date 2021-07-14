package yejun.api.enrolment;

import yejun.api.common.Semester;

import java.util.Calendar;

public class EnrolmentRequestDTO {
    private Integer studentId;
    private Long courseId;
    private int year;
    private Semester semester;
    private int page;
    private int size;

    public EnrolmentRequestDTO() {
        studentId = null;
        courseId = null;
        year = Calendar.getInstance().get(Calendar.YEAR);
        semester = Semester.getSemesterNow();
        page = 0;
        size = 20;
    }

    public EnrolmentRequestDTO(Integer studentId, Long courseId, int year, Semester semester, int page, int size) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.page = page;
        this.size = size;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
