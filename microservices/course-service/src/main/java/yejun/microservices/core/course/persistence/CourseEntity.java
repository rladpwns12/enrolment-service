package yejun.microservices.core.course.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import yejun.api.common.Department;

import static java.lang.String.format;

@Document(collection="courses")
public class CourseEntity {
    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private Long courseId;

    private Department department;
    private String title;
    private String professorName;
    private int credit;
    private int numberOfStudents;
    private int spare;

    public CourseEntity() {
    }

    public CourseEntity(Long courseId, Department department, String title, String professorName, int credit, int numberOfStudents, int spare) {
        this.courseId = courseId;
        this.department = department;
        this.title = title;
        this.professorName = professorName;
        this.credit = credit;
        this.numberOfStudents = numberOfStudents;
        this.spare = spare;
    }

    @Override
    public String toString() {
        return format("CourseEntity: %s", courseId);
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
