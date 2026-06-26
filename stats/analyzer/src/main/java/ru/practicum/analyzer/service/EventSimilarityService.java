package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Service
@RequiredArgsConstructor
public class EventSimilarityService {

    private final EventSimilarityRepository repository;

    @Transactional
    public void save(EventSimilarityAvro similarity) {
        long eventA = Math.min(similarity.getEventA(), similarity.getEventB());
        long eventB = Math.max(similarity.getEventA(), similarity.getEventB());

        EventSimilarity eventSimilarity = repository.findByEventAAndEventB(eventA, eventB)
                .orElseGet(EventSimilarity::new);

        eventSimilarity.setEventA(eventA);
        eventSimilarity.setEventB(eventB);
        eventSimilarity.setScore(similarity.getScore());
        eventSimilarity.setTimestamp(similarity.getTimestamp());

        repository.save(eventSimilarity);
    }
}