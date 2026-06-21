package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventUpdateCommand {

    private Long userId;
    private Long eventId;
    private UpdateEventUserRequest request;
}
