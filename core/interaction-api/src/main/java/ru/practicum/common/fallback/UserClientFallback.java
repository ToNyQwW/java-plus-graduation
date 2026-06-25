package ru.practicum.common.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.common.user.UserClient;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.FeignClientUnavailableException;

import java.util.List;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDto registerUser(NewUserRequest newUser) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public void delete(Long userId) {
        logError();
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    void logError() {
        log.error("Fallback response: user service is unavailable");
    }
}