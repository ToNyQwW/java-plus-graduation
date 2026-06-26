package ru.practicum.analyzer.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
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

    @PreDestroy
    public void shutdown() {
        userActionConsumer.wakeup();
        eventSimilarityConsumer.wakeup();
    }

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
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<Long, UserActionAvro> records = userActionConsumer.poll(kafkaPollTimeout);

                if (!records.isEmpty()) {
                    records.forEach(record -> userInteractionService.save(record.value()));

                    userActionConsumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка обработки действий пользователей в Analyzer", e);
        } finally {
            userActionConsumer.close();
        }
    }

    private void pollEventSimilarities() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<Long, EventSimilarityAvro> records = eventSimilarityConsumer.poll(kafkaPollTimeout);

                if (!records.isEmpty()) {
                    records.forEach(record -> eventSimilarityService.save(record.value()));

                    eventSimilarityConsumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка обработки сходства мероприятий в Analyzer", e);
        } finally {
            eventSimilarityConsumer.close();
        }
    }
}