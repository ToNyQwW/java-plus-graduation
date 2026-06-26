package ru.practicum.analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "analyzer.kafka")
public record AnalyzerKafkaProperties(
        String bootstrapServers,
        String userActionsGroupId,
        String eventsSimilarityGroupId,
        Topics topics,
        long pollTimeoutMs
) {

    public record Topics(
            String userActions,
            String eventsSimilarity
    ) {
    }
}