package ru.practicum.client.internal.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.client.internal.RequestClientInternal;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.exception.FeignClientUnavailableException;

import java.util.Map;
import java.util.Set;

@Component
public class RequestClientFallbackInternal implements RequestClientInternal {

    @Override
    public Long countByStatus(Long eventId, ParticipationRequestStatus status) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public Long getConfirmedRequestsCount(Long eventId) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public Map<Long, Long> getEventIdToConfirmedRequestsCount(Set<Long> eventIds) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public boolean existsByRequesterIdInternal(Long requesterId) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }
}