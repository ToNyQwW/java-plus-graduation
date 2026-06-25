package ru.practicum.collector.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;


@Component
public class UserActionMapper {

    public UserActionAvro toAvro(UserActionProto proto) {
        return new UserActionAvro(
                proto.getUserId(),
                proto.getEventId(),
                toAvroActionType(proto.getActionType()),
                toInstant(proto.getTimestamp())
        );
    }

    private ActionTypeAvro toAvroActionType(ActionTypeProto actionType) {
        return switch (actionType) {
            case VIEW -> ActionTypeAvro.VIEW;
            case REGISTER -> ActionTypeAvro.REGISTER;
            case LIKE -> ActionTypeAvro.LIKE;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Неизвестный тип действия пользователя");
        };
    }

    private Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}