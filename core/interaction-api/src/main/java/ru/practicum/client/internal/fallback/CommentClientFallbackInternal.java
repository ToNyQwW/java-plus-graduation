package ru.practicum.client.internal.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.client.internal.CommentClientInternal;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.FeignClientUnavailableException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CommentClientFallbackInternal implements CommentClientInternal {

    @Override
    public Map<Long, List<CommentDto>> getEventIdToCommentsDtoMap(Set<Long> eventIds) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }

    @Override
    public boolean existsByAuthorIdInternal(Long authorId) {
        throw new FeignClientUnavailableException("Сервис временно недоступен");
    }
}