package ru.practicum.client.internal.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.client.internal.EventClientInternal;
import ru.practicum.dto.event.EventInternalDto;
import ru.practicum.exception.FeignClientUnavailableException;

@Component
public class EventClientFallbackInternal implements EventClientInternal {

    @Override
    public EventInternalDto getEventByIdInternal(Long eventId) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public EventInternalDto getExistingEventInternal(Long categoryId, Long initiatorId) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }
}