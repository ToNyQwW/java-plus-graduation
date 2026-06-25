package ru.practicum.internal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.config.FeignCustomConfig;
import ru.practicum.internal.fallback.RequestClientFallbackInternal;

import java.util.Map;
import java.util.Set;

@FeignClient(
        name = "request-service-internal",
        url = "http://localhost:8080",
        path = "/internal/request",
        fallback = RequestClientFallbackInternal.class,
        configuration = FeignCustomConfig.class)
public interface RequestClientInternal {

    @GetMapping("/{eventId}/confirmed")
    Long getConfirmedRequestsCount(@PathVariable Long eventId);

    @GetMapping("/confirmed")
    Map<Long, Long> getEventIdToConfirmedRequestsCount(@RequestBody Set<Long> eventIds);

    @GetMapping("/exists")
    boolean existsByRequesterIdInternal(@RequestParam Long requesterId);
}