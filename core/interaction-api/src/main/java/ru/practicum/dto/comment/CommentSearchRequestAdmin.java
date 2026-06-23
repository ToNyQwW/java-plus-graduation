package ru.practicum.dto.comment;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.enums.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class CommentSearchRequestAdmin {

    @Size(min = 1, max = 2000)
    private String text;

    private List<Long> eventIds;

    @PositiveOrZero
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    private List<CommentStatus> statusList;

    @PositiveOrZero
    private int from;

    @Positive
    private int size;

    public CommentSearchRequestAdmin(String text, List<Long> eventIds, Long userId, LocalDateTime rangeStart, LocalDateTime rangeEnd, List<CommentStatus> statusList, Integer from, Integer size) {
        this.text = text;
        this.eventIds = eventIds;
        this.userId = userId;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.statusList = statusList;
        this.from = from == null ? 0 : from;
        this.size = size == null ? 10 : size;
    }
}