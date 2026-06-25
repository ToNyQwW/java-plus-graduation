package ru.practicum.client.internal.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.client.internal.CategoryClientInternal;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.FeignClientUnavailableException;

import java.util.Map;
import java.util.Set;

@Component
public class CategoryClientFallbackInternal implements CategoryClientInternal {

    @Override
    public CategoryDto getCategory(Long categoryId) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public Map<Long, CategoryDto> getCategoryIdToCategoryDtoMap(Set<Long> categoryIds) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }
}