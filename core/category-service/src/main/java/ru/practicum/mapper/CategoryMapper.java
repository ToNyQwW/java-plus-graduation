package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.Category;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryDto mapCategoryToCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category mapNewCategoryDtoToCategory(NewCategoryDto newCategoryDto);
}