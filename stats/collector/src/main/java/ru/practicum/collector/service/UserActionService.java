package ru.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.collector.kafka.UserActionKafkaSender;
import ru.practicum.collector.mapper.UserActionMapper;
import ru.practicum.ewm.stats.proto.UserActionProto;

@Service
@RequiredArgsConstructor
public class UserActionService {

    private final UserActionMapper mapper;
    private final UserActionKafkaSender kafkaSender;

    public void collect(UserActionProto action) {
        kafkaSender.send(mapper.toAvro(action));
    }
}