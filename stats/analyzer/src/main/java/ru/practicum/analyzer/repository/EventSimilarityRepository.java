package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.analyzer.model.EventSimilarity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    Optional<EventSimilarity> findByEventAAndEventB(Long eventA, Long eventB);

    @Query("""
            select similarity
            from EventSimilarity similarity
            where similarity.eventA = :eventId or similarity.eventB = :eventId
            order by similarity.score desc
            """)
    List<EventSimilarity> findByEventIdOrderByScoreDesc(Long eventId);

    @Query("""
            select similarity
            from EventSimilarity similarity
            where (similarity.eventA = :eventA and similarity.eventB = :eventB)
               or (similarity.eventA = :eventB and similarity.eventB = :eventA)
            """)
    Optional<EventSimilarity> findByEventIds(Long eventA, Long eventB);

    @Query("""
            select similarity
            from EventSimilarity similarity
            where similarity.eventA in :eventIds
               or similarity.eventB in :eventIds
            """)
    List<EventSimilarity> findAllByEventIds(@Param("eventIds") Collection<Long> eventIds);

    @Query("""
            select similarity
            from EventSimilarity similarity
            where
                (similarity.eventA in :candidateIds and similarity.eventB in :userEventIds)
             or (similarity.eventB in :candidateIds and similarity.eventA in :userEventIds)
            """)
    List<EventSimilarity> findBetweenEvents(@Param("candidateIds") Collection<Long> candidateIds,
                                            @Param("userEventIds") Collection<Long> userEventIds);
}