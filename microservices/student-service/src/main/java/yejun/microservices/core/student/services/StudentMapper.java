package yejun.microservices.core.student.services;

import org.mapstruct.*;
import yejun.api.student.Student;
import yejun.microservices.core.student.persistence.StudentEntity;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Student entityToApi(StudentEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    StudentEntity apiToEntity(Student api);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "studentId", ignore = true)
    })
    StudentEntity updateEntity(Student api, @MappingTarget StudentEntity entity);
}
