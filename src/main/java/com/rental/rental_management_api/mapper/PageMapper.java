package com.rental.rental_management_api.mapper;

import com.rental.rental_management_api.payload.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PageMapper {

    default <T, D> PageResponse<D> toPageResponse(Page<T> page, java.util.List<D> dtoList) {
        return new PageResponse<>(
                dtoList,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}