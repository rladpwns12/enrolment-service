package yejun.microservices.core.student.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import yejun.api.common.Department;

import static java.lang.String.format;

@Document(collection="students")
public class StudentEntity {
    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int studentId;

    private String email;
    private String name;
    private String password;
    private Department department;

    public StudentEntity() {
    }

    public StudentEntity(int studentId, String name, String email, String password, Department department) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.department = department;
    }

    @Override
    public String toString() {
        return format("StudentEntity: %s", studentId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
