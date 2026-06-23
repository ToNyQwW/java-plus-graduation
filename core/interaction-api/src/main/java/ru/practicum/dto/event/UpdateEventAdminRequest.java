package ru.practicum.dto.event;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.dto.location.Location;

@Data
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    @PositiveOrZero
    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;
}