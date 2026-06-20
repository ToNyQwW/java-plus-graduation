package ru.practicum.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constants.DatePatternConstant;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.NewEndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-server", fallback = StatClientFallback.class)
public interface StatClient {

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    EndpointHitDto hit(@RequestBody @Valid NewEndpointHitDto newEndpointHitDto) throws FeignException;

    @GetMapping("/stats")
    List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = DatePatternConstant.DATE_TIME_PATTERN) LocalDateTime start,
                                @RequestParam @DateTimeFormat(pattern = DatePatternConstant.DATE_TIME_PATTERN) LocalDateTime end,
                                @RequestParam(required = false) List<String> uris,
                                @RequestParam(defaultValue = "false") Boolean unique
    ) throws FeignException;

    @GetMapping("/stats/all")
    List<ViewStatsDto> getAllStats(@RequestParam(required = false) List<String> uris,
                                   @RequestParam Boolean unique
    ) throws FeignException;
}