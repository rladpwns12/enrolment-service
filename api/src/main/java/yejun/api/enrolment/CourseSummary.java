package yejun.api.enrolment;

import yejun.api.common.Department;
import yejun.api.common.Semester;

public class CourseSummary {

    private Long courseId;
    private Department department;
    private String title;
    private String professorName;
    private int credit;
    private int numberOfStudents;
    private int spare;
    private int capacity;
    private int year;
    private Semester semester;

    public CourseSummary() {
        courseId = null;
        department = null;
        title = null;
        professorName = null;
        credit = 0;
        numberOfStudents = 0;
        spare = 0;
        capacity = 0;
        year = 0;
        semester = null;
    }

    public CourseSummary(Long courseId, Department department, String title, String professorName, int credit, int numberOfStudents, int spare, int capacity, int year, Semester semester
    ) {
        this.courseId = courseId;
        this.department = department;
        this.title = title;
        this.professorName = professorName;
        this.credit = credit;
        this.numberOfStudents = numberOfStudents;
        this.spare = spare;
        this.capacity = capacity;
        this.year = year;
        this.semester = semester;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public int getSpare() {
        return spare;
    }

    public void setSpare(int spare) {
        this.spare = spare;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
