package naumen.project.mapper;

import naumen.project.dto.paged.PagedResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PageMapper {

    @Mapping(source = "number", target = "page")
    <T> PagedResponseDto<T> toResponse(Page<T> request);
}
