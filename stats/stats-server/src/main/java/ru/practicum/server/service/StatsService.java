package ru.practicum.server.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.NewEndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    EndpointHitDto createEndpointHit(NewEndpointHitDto newEndpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}