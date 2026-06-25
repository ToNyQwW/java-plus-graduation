package ru.practicum.controller.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.UserService;
import ru.practicum.client.internal.UserClientInternal;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/internal/users")
public class UserControllerInt implements UserClientInternal {
    private final UserService userService;

    @Override
    @GetMapping("/{userId}")
    public UserShortDto getUserShortInfoById(@PathVariable Long userId) {
        log.info("Получен запрос на получение UserShortDto, userId={}", userId);
        return userService.getUserShortInfoById(userId);
    }

    @Override
    @GetMapping
    public List<UserShortDto> getUserShortInfo(@RequestBody Set<Long> userIds) {
        log.info("Получен запрос на получение списка UserShortDto, userIds={}", userIds);
        return userService.getUserShortInfo(userIds);
    }

    @Override
    public Map<Long, UserShortDto> userIdToUserShortDtoMap(Set<Long> userIds) {
        log.info("Получен запрос на получение словаря UserShortDto, userIds={}", userIds);
        return userService.userIdToUserShortDtoMap(userIds);
    }
}