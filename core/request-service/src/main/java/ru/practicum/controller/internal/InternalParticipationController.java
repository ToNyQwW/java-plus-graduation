package ru.practicum.controller.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.internal.RequestClientInternal;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.service.ParticipationRequestService;

import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/internal/request")
public class InternalParticipationController implements RequestClientInternal {
    private final ParticipationRequestService service;
    private final ParticipationRequestRepository repository;

    @GetMapping("/event/{eventId}/count/{status}")
    public Long countByStatus(@PathVariable Long eventId, @PathVariable ParticipationRequestStatus status) {
        return repository.countByEventIdAndStatus(eventId, status);
    }

    @GetMapping("/{eventId}/confirmed")
    public Long getConfirmedRequestsCount(@PathVariable Long eventId) {
        log.info("Получение количества подтвержденных запросов на участие в событии, eventId={}", eventId);
        return service.getConfirmedRequestsCount(eventId);
    }

    @GetMapping("/confirmed")
    public Map<Long, Long> getEventIdToConfirmedRequestsCount(@RequestBody Set<Long> eventIds) {
        return service.getEventIdToConfirmedRequestsCount(eventIds);
    }

    @GetMapping("/exists")
    public boolean existsByRequesterIdInternal(@RequestParam Long requesterId) {
        return service.existsByRequesterIdInternal(requesterId);
    }
}