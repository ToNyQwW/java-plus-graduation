package ru.practicum.client;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.NewEndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class StatClientFallback implements StatClient {

    @Override
    public EndpointHitDto hit(NewEndpointHitDto newEndpointHitDto) {
        log.error("Fallback response: stats server is unavailable");
        return null;
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.error("Fallback response: stats server is unavailable");
        return null;
    }

    @Override
    public List<ViewStatsDto> getAllStats(List<String> uris, Boolean unique) throws FeignException {
        log.error("Fallback response: stats server is unavailable");
        return null;
    }
}
