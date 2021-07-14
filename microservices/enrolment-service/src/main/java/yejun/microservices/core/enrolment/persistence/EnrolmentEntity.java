package yejun.microservices.core.enrolment.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import yejun.api.common.Department;

import static java.lang.String.format;

@Document(collection="enrolments")
@CompoundIndex(name = "cors-stud-id", unique = true, def = "{'courseId' : 1, 'studentId': 1}")
public class EnrolmentEntity {
    @Id
    private String id;

    @Version
    private Integer version;

    private Long courseId;

    private int studentId;


    public EnrolmentEntity() {
    }

    public EnrolmentEntity(Long courseId, int studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return format("EnrolmentEntity: %s/%d", courseId, studentId);
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

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }


}
