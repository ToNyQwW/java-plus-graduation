package ru.practicum.analyzer.model;

public record SimilarityKey(Long eventA, Long eventB) {

    public SimilarityKey {
        long min = Math.min(eventA, eventB);
        long max = Math.max(eventA, eventB);

        eventA = min;
        eventB = max;
    }
}
