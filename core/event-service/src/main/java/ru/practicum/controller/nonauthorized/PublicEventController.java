package ru.practicum.controller.nonauthorized;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.common.nonauthorized.EventClientNonauthorized;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventSearchRequestUser;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.exception.FeignClientUnavailableException;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController implements EventClientNonauthorized {

    private final EventService eventService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request,
                                 @RequestHeader("X-EWM-USER-ID") Long userId) {
        log.info("Получение информации о событии {}", id);
        return eventService.getPublicEvent(id, request.getRemoteAddr(), userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> searchForUser(@RequestParam(required = false) String text,
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
                                             HttpServletRequest request) {
        log.info("Получение событий публичным эндпоинтом");
        EventSearchRequestUser param = new EventSearchRequestUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Сформирован DTO с параметрами запроса {}", param);

        List<EventShortDto> resp = eventService.searchForUser(param);

        try {
            log.info("Добавление события getEvent в сервис статистики завершено успешно");
            return resp;
        } catch (FeignException e) {
            log.error("Ошибка feign-клиента сервиса статистики: {}", e.getMessage());
            throw new FeignClientUnavailableException(e.getMessage());
        }
    }

    @GetMapping("/recommendations")
    public Stream<RecommendedEventProto> getRecommendations(
            @RequestHeader("X-EWM-USER-ID") Long userId,
            @RequestParam(defaultValue = "10") int maxResults) {

        log.debug("Controller: getRecommendations userId={}, maxResults={}", userId, maxResults);
        return eventService.getRecommendationsForUser(userId, maxResults);
    }

    @PutMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.OK)
    public void likeEvent(
            @PathVariable Long eventId,
            @RequestHeader("X-EWM-USER-ID") Long userId) {

        log.debug("Controller: likeEvent eventId={}, userId={}", eventId, userId);
        eventService.likeEvent(userId, eventId);
    }
}