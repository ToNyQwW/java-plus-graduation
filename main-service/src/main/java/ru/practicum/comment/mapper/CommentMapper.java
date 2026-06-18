package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.ShortCommentDto;
import ru.practicum.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "eventId", source = "event.id")
    CommentDto toDto(Comment comment);

    Comment toEntity(NewCommentDto newCommentDto);

    @Mapping(target = "eventId", source = "event.id")
    ShortCommentDto toShortDto(Comment comment);
}