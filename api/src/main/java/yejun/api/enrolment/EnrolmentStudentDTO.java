package yejun.api.enrolment;

import yejun.api.common.Semester;

import java.util.Calendar;

public class EnrolmentStudentDTO {

    private Integer studentId;
    private int year;
    private Semester semester;

    public EnrolmentStudentDTO() {
        studentId = null;
        year = Calendar.getInstance().get(Calendar.YEAR);
        semester = Semester.getSemesterNow();
    }

    public EnrolmentStudentDTO(Integer studentId, int year, Semester semester) {
        this.studentId = studentId;
        this.year = year;
        this.semester = semester;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
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
}
