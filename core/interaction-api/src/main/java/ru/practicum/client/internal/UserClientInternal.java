package ru.practicum.client.internal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.client.config.FeignCustomConfig;
import ru.practicum.client.internal.fallback.UserClientFallbackInternal;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(
        name = "user-service-internal",
        url = "http://localhost:8080",
        path = "/internal/users",
        fallback = UserClientFallbackInternal.class,
        configuration = FeignCustomConfig.class)
public interface UserClientInternal {

    @GetMapping("/{userId}")
    UserShortDto getUserShortInfoById(@PathVariable Long userId);

    @GetMapping
    List<UserShortDto> getUserShortInfo(@RequestBody Set<Long> userIds);

    @GetMapping("/map")
    Map<Long, UserShortDto> userIdToUserShortDtoMap(@RequestBody Set<Long> userIds);
}