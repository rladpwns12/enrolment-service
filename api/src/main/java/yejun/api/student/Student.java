package yejun.api.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import yejun.api.common.Department;

public class Student {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer studentId;
    private String name;
    private String email;
    private String password;
    private Department department;
    @ApiModelProperty(hidden = true)
    private String serviceAddress;

    public Student() {
        studentId = null;
        name = null;
        department = null;
        serviceAddress = null;
    }

    public Student(Integer studentId, String name, String email, String password, Department department, String serviceAddress) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.department = department;
        this.serviceAddress = serviceAddress;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Department getdepartment() {
        return department;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setdepartment(Department department) {
        this.department = department;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

}
