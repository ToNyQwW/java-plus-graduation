package ru.practicum.controller.authorized;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.common.event.authorized.EventClientAuthorized;
import ru.practicum.service.EventService;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventController implements EventClientAuthorized {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Запрос от Пользователя {} на создание события {}", userId, newEventDto);
        return eventService.create(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateByUser(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @Valid @RequestBody UpdateEventUserRequest request) {
        log.info("Пользователь с id {}, обновил событие с id {}", userId, eventId);
        return eventService.updateByUser(userId, eventId, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос от Пользователя {} на получение информации о событии {}", userId, eventId);
        return eventService.getByUser(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllByUser(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение событий, добавленных Пользователем {}", userId);
        return eventService.getAllByUser(userId, from, size);
    }
}