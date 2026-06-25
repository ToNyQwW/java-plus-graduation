package ru.practicum.controller.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.internal.EventClientInternal;
import ru.practicum.service.EventService;
import ru.practicum.dto.event.EventInternalDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/internal/events")
public class InternalEventController implements EventClientInternal {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EventInternalDto getEventByIdInternal(@PathVariable Long eventId) {
        log.info("Запрос от на получение информации о событии eventId={}", eventId);
        return eventService.getEventByIdInternal(eventId);
    }

    @GetMapping
    public EventInternalDto getExistingEventInternal(@RequestParam(required = false) Long categoryId,
                                                     @RequestParam(required = false) Long initiatorId) {
        log.info("Проверку существования связанных событий");
        return eventService.getExistingEventInternal(categoryId, initiatorId);
    }
}