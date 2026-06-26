package ru.practicum.analyzer.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.analyzer.service.RecommendationsService;
import ru.practicum.ewm.stats.proto.*;

@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcService extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationsService recommendationsService;

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            recommendationsService
                    .getInteractionsCount(request.getEventIdList())
                    .forEach(responseObserver::onNext);

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to get interactions count")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            recommendationsService
                    .getSimilarEvents(request.getUserId(), request.getEventId(), request.getMaxResults())
                    .forEach(responseObserver::onNext);

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to get similar events")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {

            recommendationsService
                    .getRecommendationsForUser(request.getUserId(), request.getMaxResults())
                    .forEach(responseObserver::onNext);

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Failed to get recommendations")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }
}