package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.Comment;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentDtoAdmin;
import ru.practicum.dto.comment.NewCommentDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", source = "userId")
    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "status", ignore = true)
    Comment mapToComment(NewCommentDto comment, Long userId, Long eventId);

    @Mapping(target = "authorName", source = "authorName")
    CommentDto mapToCommentDto(Comment comment, String authorName);

    CommentDtoAdmin mapToCommentDtoAdmin(Comment comment);
}