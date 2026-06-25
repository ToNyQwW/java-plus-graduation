package ru.practicum.service;

import ru.practicum.dto.event.*;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;

import java.util.List;
import java.util.stream.Stream;

public interface EventService {

    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto updateByUser(Long userId, Long eventId, UpdateEventUserRequest request);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest request);

    EventFullDto getByUser(Long userId, Long eventId);

    EventFullDto getPublicEvent(Long eventId, String ip, Long userId);

    List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size);

    List<EventFullDto> searchForAdmin(EventSearchRequestAdmin param);

    List<EventShortDto> searchForUser(EventSearchRequestUser param);

    EventInternalDto getEventByIdInternal(Long eventId);

    EventInternalDto getExistingEventInternal(Long categoryId, Long initiatorId);

    Stream<RecommendedEventProto> getRecommendationsForUser(Long userId, int maxResults);

    void likeEvent(Long userId, Long eventId);
}