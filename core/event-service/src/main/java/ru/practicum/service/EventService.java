package ru.practicum.service;

import ru.practicum.dto.event.*;

import java.util.List;

public interface EventService {

    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto updateByUser(EventUpdateCommand command);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest request);

    EventFullDto getByUser(Long userId, Long eventId);

    EventFullDto getPublicEvent(Long eventId);

    List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size);

    List<EventFullDto> searchForAdmin(EventSearchRequestAdmin param);

    List<EventShortDto> searchForUser(EventSearchRequestUser param);

    EventInternalDto getEventByIdInternal(Long eventId);

    EventInternalDto getExistingEventInternal(Long categoryId, Long initiatorId);
}