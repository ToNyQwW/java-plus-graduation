package ru.practicum.analyzer.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.SimilarityKey;
import ru.practicum.analyzer.model.UserInteraction;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.repository.UserInteractionRepository;
import ru.practicum.ewm.stats.proto.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcService extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final UserInteractionRepository userInteractionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            Map<Long, Double> weightsByEvent = userInteractionRepository
                    .sumWeightsByEventIds(request.getEventIdList())
                    .stream()
                    .collect(Collectors.toMap(
                            row -> (Long) row[0],
                            row -> ((Number) row[1]).doubleValue()
                    ));

            request.getEventIdList().forEach(eventId ->
                    responseObserver.onNext(
                            RecommendedEventProto.newBuilder()
                                    .setEventId(eventId)
                                    .setScore(weightsByEvent.getOrDefault(eventId, 0.0))
                                    .build()));

            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to get interactions count")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            validateMaxResults(request.getMaxResults());

            Set<Long> interactedEventIds = userInteractionRepository
                    .findByUserId(request.getUserId())
                    .stream()
                    .map(UserInteraction::getEventId)
                    .collect(Collectors.toSet());

            eventSimilarityRepository
                    .findByEventIdOrderByScoreDesc(request.getEventId())
                    .stream()
                    .map(similarity -> toRecommendedSimilarEvent(request.getEventId(), similarity))
                    .filter(recommendation -> !interactedEventIds.contains(recommendation.getEventId()))
                    .limit(request.getMaxResults())
                    .forEach(responseObserver::onNext);

            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to get similar events")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            validateMaxResults(request.getMaxResults());

            List<UserInteraction> interactions = userInteractionRepository.findByUserId(request.getUserId());

            if (interactions.isEmpty()) {
                responseObserver.onCompleted();
                return;
            }

            Set<Long> userEventIds = interactions.stream()
                    .map(UserInteraction::getEventId)
                    .collect(Collectors.toSet());

            List<EventSimilarity> relatedSimilarities = eventSimilarityRepository.findAllByEventIds(userEventIds);

            Set<Long> candidateEventIds = relatedSimilarities.stream()
                    .map(similarity -> getOtherEventId(similarity, userEventIds))
                    .filter(id -> !userEventIds.contains(id))
                    .collect(Collectors.toSet());

            List<EventSimilarity> candidateSimilarities =
                    eventSimilarityRepository.findBetweenEvents(candidateEventIds, userEventIds);

            Map<SimilarityKey, Double> similarityMap = candidateSimilarities.stream()
                    .collect(Collectors.toMap(
                            similarity -> new SimilarityKey(similarity.getEventA(), similarity.getEventB()),
                            EventSimilarity::getScore, (a, b) -> a
                    ));

            candidateEventIds.stream()
                    .map(candidate -> toPredictedRecommendation(candidate, interactions, similarityMap))
                    .filter(recommendation -> recommendation.getScore() > 0)
                    .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                    .limit(request.getMaxResults())
                    .forEach(responseObserver::onNext);

            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to get recommendations")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    private RecommendedEventProto toRecommendedSimilarEvent(long eventId, EventSimilarity similarity) {
        long similarEventId = getOtherEventId(similarity, eventId);

        return RecommendedEventProto.newBuilder()
                .setEventId(similarEventId)
                .setScore(similarity.getScore())
                .build();
    }

    private RecommendedEventProto toPredictedRecommendation(Long candidateEventId,
                                                            List<UserInteraction> interactions,
                                                            Map<SimilarityKey, Double> similarityMap) {
        double weightedScoreSum = 0.0;
        double similaritySum = 0.0;

        for (UserInteraction interaction : interactions) {
            double similarity = similarityMap.getOrDefault(
                    new SimilarityKey(candidateEventId, interaction.getEventId()),
                    0.0
            );

            weightedScoreSum += interaction.getWeight() * similarity;
            similaritySum += similarity;
        }

        double score = similaritySum == 0.0 ? 0.0 : weightedScoreSum / similaritySum;

        return RecommendedEventProto.newBuilder()
                .setEventId(candidateEventId)
                .setScore(score)
                .build();
    }

    private Long getOtherEventId(EventSimilarity similarity, Set<Long> interactedEventIds) {
        return interactedEventIds.contains(similarity.getEventA())
                ? similarity.getEventB()
                : similarity.getEventA();
    }

    private Long getOtherEventId(EventSimilarity similarity, long eventId) {
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