package ru.practicum.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.service.SimilarityService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionConsumerRunner implements ApplicationRunner {

    private final Duration kafkaPollTimeout;
    private final SimilarityService similarityService;
    private final EventSimilaritySender eventSimilaritySender;
    private final KafkaConsumer<Long, UserActionAvro> consumer;

    @Override
    public void run(ApplicationArguments args) {
        Thread consumerThread = new Thread(this::pollUserActions, "user-action-consumer");
        consumerThread.setDaemon(false);
        consumerThread.start();
    }

    private void pollUserActions() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(kafkaPollTimeout);

                records.forEach(record -> {
                    UserActionAvro action = record.value();
                    List<EventSimilarityAvro> similarities = similarityService.updateSimilarities(action);
                    similarities.forEach(eventSimilaritySender::send);
                    eventSimilaritySender.flush();
                });
            } catch (Exception e) {
                log.error("Ошибка обработки действий пользователей в Aggregator", e);
            }
        }
    }
}