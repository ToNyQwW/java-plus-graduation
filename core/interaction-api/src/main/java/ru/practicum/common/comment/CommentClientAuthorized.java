package ru.practicum.common.comment;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.fallback.CommentFallback;
import ru.practicum.config.FeignCustomConfig;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;

@FeignClient(
        name = "comment-service-authorized",
        url = "http://localhost:8080",
        path = "/users/{userId}/comments",
        fallback = CommentFallback.class,
        configuration = FeignCustomConfig.class)
public interface CommentClientAuthorized {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto addComment(@PathVariable Long userId,
                          @RequestParam Long eventId,
                          @Valid @RequestBody NewCommentDto newDto);

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    CommentDto updateComment(@PathVariable Long userId,
                             @PathVariable Long commentId,
                             @Valid @RequestBody NewCommentDto newDto);

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCommentByUser(@PathVariable Long userId, @PathVariable Long commentId);
}