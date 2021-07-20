package yejun.api.course;

import yejun.api.common.Department;
import yejun.api.common.Semester;

public class Course {
    private Long courseId;
    private Department department;
    private String title;
    private String professorName;
    private Integer credit;
    private Integer numberOfStudents;
    private Integer spare;
    private Integer capacity;
    private Integer year;
    private Semester semester;
    private String serviceAddress;

    public Course() {
        courseId = null;
        department = null;
        title = null;
        professorName = null;
        credit = null;
        numberOfStudents = null;
        spare = null;
        capacity = null;
        year = null;
        semester = null;
        serviceAddress = null;
    }

    public Course(Long courseId, Department department, String title, String professorName, Integer credit,
                  Integer numberOfStudents, Integer spare, Integer capacity, Integer year, Semester semester, String serviceAddress
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
        this.serviceAddress = serviceAddress;
    }

    public Course(Long courseId, Integer numberOfStudents){
        this.courseId = courseId;
        this.numberOfStudents = numberOfStudents;
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

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public Integer getSpare() {
        return spare;
    }

    public void setSpare(Integer spare) {
        this.spare = spare;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
