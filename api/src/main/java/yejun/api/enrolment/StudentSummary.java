package yejun.api.enrolment;

import yejun.api.common.Department;

public class StudentSummary {
    private Integer studentId;
    private String name;
    private String email;
    private Department department;

    public StudentSummary() {
        studentId = null;
        name = null;
        department = null;
    }

    public StudentSummary(Integer studentId, String name, String email, Department department) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
