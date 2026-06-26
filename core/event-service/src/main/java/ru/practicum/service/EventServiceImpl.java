package ru.practicum.service;

import com.google.protobuf.Timestamp;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.AnalyzerClient;
import ru.practicum.CollectorClient;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventState;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    private final EventRepository eventRepository;

    private final AnalyzerClient analyzerClient;
    private final CollectorClient collectorClient;

    private final HelperService helperService;

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER);
        HelperService.checkEventDateIsValid(eventDate);
        Event event = eventRepository.save(EventMapper.mapToEvent(newEventDto, userId, eventDate));
        event.setRating(0.0);
        EventFullDto dto = helperService.getEventFullDto(event);
        log.info("Создание события {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventUserRequest request) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        helperService.updateEventFieldsFromUserRequest(request, event);
        Event eventSaved = eventRepository.save(event);
        EventFullDto dto = helperService.getEventFullDto(eventSaved);

        log.info("Пользователь: Обновление события {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = helperService.getEvent(eventId);
        helperService.updateEventFieldsFromAdminRequest(request, event);

        EventFullDto dto = helperService.getEventFullDto(event);

        log.info("Администратор: Обновление события {}", dto);
        return dto;
    }

    @Override
    public EventFullDto getByUser(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        EventFullDto dto = helperService.getEventFullDto(event);

        log.info("Пользователь: Получено событие {}", dto);
        return dto;
    }

    @Override
    public EventFullDto getPublicEvent(Long eventId, String ip, Long userId) {
        Event event = helperService.getEvent(eventId);
        HelperService.checkEventIsPublished(event);

        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.VIEW)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();
        collectorClient.sendUserAction(userId, eventId, action);

        EventFullDto dto = helperService.getEventFullDto(event);

        log.info("Неавторизованный пользователь: Получено событие {}", dto);
        return dto;
    }

    @Override
    public List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Set<Event> events = eventRepository.findByInitiatorId(userId, pageable).toSet();

        return helperService.getEventShortDtoList(events, false);
    }

    @Override
    public List<EventFullDto> searchForAdmin(EventSearchRequestAdmin param) {
        HelperService.checkDates(param.getRangeStart(), param.getRangeEnd());
        PageRequest page = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());

        Optional<Predicate> searchCriteriaOpt = HelperService.getAdminSearchCriteria(param);

        Set<Event> events = searchCriteriaOpt.map(predicate -> eventRepository.findAll(predicate, page).toSet())
                .orElseGet(() -> eventRepository.findAll(page).toSet());

        return helperService.getEventFullDtoList(events);
    }

    @Override
    public List<EventShortDto> searchForUser(EventSearchRequestUser param) {
        HelperService.checkDates(param.getRangeStart(), param.getRangeEnd());
        PageRequest page = HelperService.getUserSearchPage(param);
        Predicate searchCriteria = HelperService.getUserSearchCriteria(param);
        Set<Event> events = eventRepository.findAll(searchCriteria, page).toSet();
        return helperService.getEventShortDtoList(events, param.getOnlyAvailable() != null && param.getOnlyAvailable());
    }

    @Override
    public EventInternalDto getEventByIdInternal(Long eventId) {
        Event event = helperService.getEvent(eventId);
        return EventMapper.mapToInternalDto(event);
    }

    @Override
    public EventInternalDto getExistingEventInternal(Long categoryId, Long initiatorId) {
        return eventRepository.getFirstByCategoryIdOrInitiatorId(categoryId, initiatorId)
                .map(EventMapper::mapToInternalDto)
                .orElse(null);
    }

    @Override
    public Stream<RecommendedEventProto> getRecommendationsForUser(Long userId, int maxResults) {
        log.info("Получение рекомендаций для пользователя userId={}, maxResults={}", userId, maxResults);

        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        return analyzerClient.getRecommendationsForUser(request);
    }

    @Override
    @Transactional
    public void likeEvent(Long userId, Long eventId) {
        log.info("Лайк события eventId={} от пользователя userId={}", eventId, userId);

        Event event = helperService.getEvent(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Нельзя лайкнуть неопубликованное событие");
        }

        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.LIKE)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        collectorClient.sendUserAction(userId, eventId, action);
    }

}