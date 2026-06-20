package ru.practicum.client.internal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.client.config.FeignCustomConfig;
import ru.practicum.client.internal.fallback.CategoryClientFallbackInternal;
import ru.practicum.dto.category.CategoryDto;

import java.util.Map;
import java.util.Set;

@FeignClient(
        name = "category-service-internal",
        url = "http://localhost:8080",
        path = "/internal/categories",
        fallback = CategoryClientFallbackInternal.class,
        configuration = FeignCustomConfig.class
)
public interface CategoryClientInternal {

    @GetMapping("/{categoryId}")
    CategoryDto getCategory(@PathVariable Long categoryId);

    @GetMapping
    Map<Long, CategoryDto> getCategoryIdToCategoryDtoMap(@RequestBody Set<Long> categoryIds);
}