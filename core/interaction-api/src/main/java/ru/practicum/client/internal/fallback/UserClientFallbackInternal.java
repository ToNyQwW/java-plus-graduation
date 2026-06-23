package ru.practicum.client.internal.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.client.internal.UserClientInternal;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.FeignClientUnavailableException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class UserClientFallbackInternal implements UserClientInternal {

    @Override
    public UserShortDto getUserShortInfoById(Long userId) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public List<UserShortDto> getUserShortInfo(Set<Long> userIds) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public Map<Long, UserShortDto> userIdToUserShortDtoMap(Set<Long> userIds) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }
}