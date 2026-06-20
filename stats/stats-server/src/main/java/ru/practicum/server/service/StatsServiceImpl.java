package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.NewEndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.exception.BadRequestException;
import ru.practicum.server.mapper.StatMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatMapper mapper;

    @Override
    @Transactional
    public EndpointHitDto createEndpointHit(NewEndpointHitDto newEndpointHitDto) {
        EndpointHit endpointHit = mapper.mapToEndpointHit(newEndpointHitDto);
        endpointHit = statsRepository.save(endpointHit);
        return mapper.mapToEndpointHitDto(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        checkDates(start, end);

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getStatsAllWithUniqueIps(start, end);
            }
            return statsRepository.getStatsAll(start, end);
        } else if (unique) {
            return statsRepository.getStatsByUriListWithUniqueIps(start, end, uris);
        }

        return statsRepository.getStatsByUriList(start, end, uris);
    }

    private void checkDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Начальная дата не может быть позже конечной");
        }
    }
}