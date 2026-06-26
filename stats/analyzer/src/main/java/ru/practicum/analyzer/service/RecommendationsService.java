package ru.practicum.analyzer.service;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.SimilarityKey;
import ru.practicum.analyzer.model.UserInteraction;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.repository.UserInteractionRepository;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationsService {

    private final UserInteractionRepository userInteractionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    public List<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        Map<Long, Double> weightsByEvent = userInteractionRepository
                .sumWeightsByEventIds(eventIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).doubleValue()
                ));

        return eventIds.stream()
                .map(eventId -> RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(weightsByEvent.getOrDefault(eventId, 0.0))
                        .build())
                .toList();
    }

    public List<RecommendedEventProto> getSimilarEvents(long userId,
                                                        long eventId,
                                                        int maxResults) {

        validateMaxResults(maxResults);

        Set<Long> interactedEventIds = userInteractionRepository.findByUserId(userId)
                .stream()
                .map(UserInteraction::getEventId)
                .collect(Collectors.toSet());

        return eventSimilarityRepository.findByEventIdOrderByScoreDesc(eventId)
                .stream()
                .map(similarity -> toRecommendedSimilarEvent(eventId, similarity))
                .filter(recommendation -> !interactedEventIds.contains(recommendation.getEventId()))
                .limit(maxResults)
                .toList();
    }

    public List<RecommendedEventProto> getRecommendationsForUser(long userId,
                                                                 int maxResults) {

        validateMaxResults(maxResults);

        List<UserInteraction> interactions = userInteractionRepository.findByUserId(userId);

        if (interactions.isEmpty()) {
            return List.of();
        }

        Set<Long> userEventIds = interactions.stream()
                .map(UserInteraction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarity> relatedSimilarities =
                eventSimilarityRepository.findAllByEventIds(userEventIds);

        Set<Long> candidateEventIds = relatedSimilarities.stream()
                .map(similarity -> getOtherEventId(similarity, userEventIds))
                .filter(id -> !userEventIds.contains(id))
                .collect(Collectors.toSet());

        if (candidateEventIds.isEmpty()) {
            return List.of();
        }

        List<EventSimilarity> candidateSimilarities =
                eventSimilarityRepository.findBetweenEvents(
                        candidateEventIds,
                        userEventIds
                );

        Map<SimilarityKey, Double> similarityMap = candidateSimilarities.stream()
                .collect(Collectors.toMap(
                        similarity -> new SimilarityKey(
                                similarity.getEventA(),
                                similarity.getEventB()
                        ),
                        EventSimilarity::getScore,
                        (a, b) -> a
                ));

        return candidateEventIds.stream()
                .map(candidate -> toPredictedRecommendation(
                        candidate,
                        interactions,
                        similarityMap))
                .filter(recommendation -> recommendation.getScore() > 0)
                .sorted(Comparator.comparingDouble(
                        RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .toList();
    }

    private RecommendedEventProto toRecommendedSimilarEvent(long eventId,
                                                            EventSimilarity similarity) {

        long similarEventId = getOtherEventId(similarity, eventId);

        return RecommendedEventProto.newBuilder()
                .setEventId(similarEventId)
                .setScore(similarity.getScore())
                .build();
    }

    private RecommendedEventProto toPredictedRecommendation(
            Long candidateEventId,
            List<UserInteraction> interactions,
            Map<SimilarityKey, Double> similarityMap) {

        double weightedScoreSum = 0.0;
        double similaritySum = 0.0;

        for (UserInteraction interaction : interactions) {

            double similarity = similarityMap.getOrDefault(
                    new SimilarityKey(candidateEventId,
                            interaction.getEventId()),
                    0.0
            );

            weightedScoreSum += interaction.getWeight() * similarity;
            similaritySum += similarity;
        }

        double score = similaritySum == 0.0
                ? 0.0
                : weightedScoreSum / similaritySum;

        return RecommendedEventProto.newBuilder()
                .setEventId(candidateEventId)
                .setScore(score)
                .build();
    }

    private Long getOtherEventId(EventSimilarity similarity,
                                 Set<Long> interactedEventIds) {

        return interactedEventIds.contains(similarity.getEventA())
                ? similarity.getEventB()
                : similarity.getEventA();
    }

    private Long getOtherEventId(EventSimilarity similarity,
                                 long eventId) {

        return similarity.getEventA().equals(eventId)
                ? similarity.getEventB()
                : similarity.getEventA();
    }

    private void validateMaxResults(int maxResults) {

        if (maxResults < 0) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("maxResults must be non-negative")
                    .asRuntimeException();
        }
    }
}
