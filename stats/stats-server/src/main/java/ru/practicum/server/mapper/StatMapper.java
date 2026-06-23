package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.NewEndpointHitDto;
import ru.practicum.server.model.EndpointHit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit mapToEndpointHit(NewEndpointHitDto dto);


    EndpointHitDto mapToEndpointHitDto(EndpointHit endpointHit);
}