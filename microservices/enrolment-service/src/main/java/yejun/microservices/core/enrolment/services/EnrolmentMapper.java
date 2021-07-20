package yejun.microservices.core.enrolment.services;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import yejun.api.course.Course;
import yejun.api.enrolment.CourseSummary;
import yejun.api.enrolment.Enrolment;
import yejun.api.enrolment.StudentSummary;
import yejun.api.student.Student;
import yejun.microservices.core.enrolment.persistence.EnrolmentEntity;

@Mapper(componentModel = "spring")
public interface EnrolmentMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Enrolment entityToApi(EnrolmentEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    EnrolmentEntity apiToEntity(Enrolment api);

    StudentSummary studentApiToSummary(Student api);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    EnrolmentEntity updateEntity(Enrolment api, @MappingTarget EnrolmentEntity entity);

    CourseSummary courseApiToSummary(Course api);
}
