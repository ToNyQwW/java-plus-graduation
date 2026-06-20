package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.EventLocation;
import ru.practicum.dto.location.Location;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    EventLocation mapLocationToEventLocation(Location location);

    Location mapEventLocationToLocation(EventLocation eventLocation);
}