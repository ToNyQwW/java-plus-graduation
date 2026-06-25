package ru.practicum.common.comment;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.fallback.CommentFallback;
import ru.practicum.config.FeignCustomConfig;
import ru.practicum.dto.comment.CommentDtoAdmin;
import ru.practicum.dto.comment.CommentSearchRequestAdmin;
import ru.practicum.dto.comment.CommentStatusChangeRequest;

import java.util.List;

@FeignClient(
        name = "comment-service-admin",
        url = "http://localhost:8080",
        path = "/admin/comments",
        fallback = CommentFallback.class,
        configuration = FeignCustomConfig.class)
public interface CommentClientAdmin {

    @PostMapping("/search")
    List<CommentDtoAdmin> getComments(@Valid @RequestBody CommentSearchRequestAdmin param);

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    List<CommentDtoAdmin> changeCommentStatus(@Valid @RequestBody CommentStatusChangeRequest dto);

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCommentByAdmin(@PathVariable Long commentId);

    @GetMapping("/{commentId}")
    CommentDtoAdmin getCommentById(@PathVariable Long commentId);
}