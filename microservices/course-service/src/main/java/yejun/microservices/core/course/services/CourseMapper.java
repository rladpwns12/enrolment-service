package yejun.microservices.core.course.services;


import org.mapstruct.*;
import yejun.api.course.Course;
import yejun.microservices.core.course.persistence.CourseEntity;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Course entityToApi(CourseEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    CourseEntity apiToEntity(Course api);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "courseId", ignore = true),
            @Mapping(target = "numberOfStudents", ignore = true),
            @Mapping(target = "spare", ignore = true)
    })
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CourseEntity updateEntity(Course api, @MappingTarget CourseEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "courseId", ignore = true),
            @Mapping(target = "spare", expression = "java(entity.getCapacity() - (api.getNumberOfStudents() == null ? 0 : api.getNumberOfStudents()))")
    })
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CourseEntity updateEntityByEnrolment(Course api, @MappingTarget CourseEntity entity);
}
