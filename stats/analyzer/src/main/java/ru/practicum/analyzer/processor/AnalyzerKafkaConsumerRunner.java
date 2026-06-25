package ru.practicum.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.service.EventSimilarityService;
import ru.practicum.analyzer.service.UserInteractionService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerKafkaConsumerRunner implements ApplicationRunner {

    private final KafkaConsumer<Long, UserActionAvro> userActionConsumer;
    private final KafkaConsumer<Long, EventSimilarityAvro> eventSimilarityConsumer;

    private final UserInteractionService userInteractionService;
    private final EventSimilarityService eventSimilarityService;

    private final Duration kafkaPollTimeout;

    @Override
    public void run(ApplicationArguments args) {
        Thread userActionsThread = new Thread(this::pollUserActions, "analyzer-user-actions-consumer");
        Thread similaritiesThread = new Thread(this::pollEventSimilarities, "analyzer-event-similarities-consumer");
        userActionsThread.setDaemon(false);
        similaritiesThread.setDaemon(false);
        userActionsThread.start();
        similaritiesThread.start();
    }

    private void pollUserActions() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<Long, UserActionAvro> records = userActionConsumer.poll(kafkaPollTimeout);
                records.forEach(record -> userInteractionService.save(record.value()));
            } catch (Exception e) {
                log.error("Ошибка обработки действий пользователей в Analyzer", e);
            }
        }
    }

    private void pollEventSimilarities() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<Long, EventSimilarityAvro> records = eventSimilarityConsumer.poll(kafkaPollTimeout);
                records.forEach(record -> eventSimilarityService.save(record.value()));
            } catch (Exception e) {
                log.error("Ошибка обработки сходства мероприятий в Analyzer", e);
            }
        }
    }
}