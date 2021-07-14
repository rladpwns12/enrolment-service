package yejun.api.course;

import yejun.api.common.Department;

public class Course {
    private Long courseId;
    private Department department;
    private String title;
    private String professorName;
    private int credit;
    private int numberOfStudents;
    private int spare;
    private String serviceAddress;

    public Course() {
        courseId = null;
        department = null;
        title = null;
        professorName = null;
        credit = 0;
        numberOfStudents = 0;
        spare = 0;
    }

    public Course(Long courseId, Department department, String title, String professorName, int credit, int numberOfStudents, int spare) {
        this.courseId = courseId;
        this.department = department;
        this.title = title;
        this.professorName = professorName;
        this.credit = credit;
        this.numberOfStudents = numberOfStudents;
        this.spare = spare;
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
}
