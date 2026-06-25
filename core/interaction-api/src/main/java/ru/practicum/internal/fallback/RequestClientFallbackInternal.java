package ru.practicum.internal.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.internal.RequestClientInternal;
import ru.practicum.exception.FeignClientUnavailableException;

import java.util.Map;
import java.util.Set;

@Component
public class RequestClientFallbackInternal implements RequestClientInternal {

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