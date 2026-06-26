package ru.practicum.client.common.fallback;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.client.common.event.admin.EventClientAdmin;
import ru.practicum.client.common.event.authorized.EventClientAuthorized;
import ru.practicum.client.common.nonauthorized.EventClientNonauthorized;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventState;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.exception.FeignClientUnavailableException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class EventClientFallback implements EventClientAdmin, EventClientAuthorized, EventClientNonauthorized {

    @Override
    public List<EventFullDto> searchForAdmin(List<Long> users, List<EventState> states,
                                             List<Long> categories, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Integer from, Integer size) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest request) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public EventFullDto getByUser(Long userId, Long eventId) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest request, @RequestHeader("X-EWM-USER-ID") Long userId) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public List<EventShortDto> searchForUser(String text, List<Long> categories,
                                             Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable,
                                             String sort, Integer from, Integer size, HttpServletRequest request) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public Stream<RecommendedEventProto> getRecommendations(Long userId, int maxResults) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public void likeEvent(Long eventId, Long userId) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    void logError() {
        log.error("Fallback response: event service is unavailable");
    }
}