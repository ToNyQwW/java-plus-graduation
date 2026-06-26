package ru.practicum.client.common.nonauthorized;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.common.fallback.EventClientFallback;
import ru.practicum.client.config.FeignCustomConfig;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@FeignClient(
        name = "event-service-nonauthorized",
        url = "http://localhost:8080",
        path = "/events",
        fallback = EventClientFallback.class,
        configuration = FeignCustomConfig.class)
public interface EventClientNonauthorized {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request, @RequestHeader("X-EWM-USER-ID") Long userId);


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventShortDto> searchForUser(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      HttpServletRequest request);

    @GetMapping("/recommendations")
    public Stream<RecommendedEventProto> getRecommendations(
            @RequestHeader("X-EWM-USER-ID") Long userId,
            @RequestParam(defaultValue = "10") int maxResults);

    @PutMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.OK)
    public void likeEvent(
            @PathVariable Long eventId,
            @RequestHeader("X-EWM-USER-ID") Long userId);
}