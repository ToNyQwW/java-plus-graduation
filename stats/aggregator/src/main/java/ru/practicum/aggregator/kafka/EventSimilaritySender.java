package ru.practicum.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilaritySender {

    private final KafkaProducer<Long, EventSimilarityAvro> producer;

    @Value("${aggregator.kafka.producer.topics.events-similarity}")
    private String eventsSimilarityTopic;

    public void send(EventSimilarityAvro similarity) {
        ProducerRecord<Long, EventSimilarityAvro> record = new ProducerRecord<>(eventsSimilarityTopic,
                similarity.getEventA(), similarity);

        try {
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Ошибка отправки сходства мероприятий в Kafka. eventA={}, eventB={}, score={}, timestamp={}",
                            similarity.getEventA(),
                            similarity.getEventB(),
                            similarity.getScore(),
                            similarity.getTimestamp(),
                            exception);
                }
            });
        } catch (Exception e) {
            throw new KafkaException("Ошибка отправки сходства мероприятий в Kafka", e);
        }
    }

    public void flush() {
        producer.flush();
    }
}