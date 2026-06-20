package ru.practicum.client.common.event.authorized;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.common.fallback.EventClientFallback;
import ru.practicum.client.config.FeignCustomConfig;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;

import java.util.List;

@FeignClient(
        name = "event-service-authorized",
        url = "http://localhost:8080",
        path = "/users/{userId}/events",
        fallback = EventClientFallback.class,
        configuration = FeignCustomConfig.class)
public interface EventClientAuthorized {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto create(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto);

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto updateByUser(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @Valid @RequestBody UpdateEventUserRequest request);

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto getByUser(@PathVariable Long userId, @PathVariable Long eventId);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventShortDto> getAllByUser(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size);
}