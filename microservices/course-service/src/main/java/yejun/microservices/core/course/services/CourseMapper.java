package yejun.microservices.core.course.services;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
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
            @Mapping(target = "numberOfStudents", ignore = true)
    })
    CourseEntity updateEntity(Course api, @MappingTarget CourseEntity entity);
}
