package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.model.UserInteraction;
import ru.practicum.analyzer.repository.UserInteractionRepository;
import ru.practicum.constant.ActionTypeWeight;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Service
@RequiredArgsConstructor
public class UserInteractionService {

    private final UserInteractionRepository repository;

    @Transactional
    public void save(UserActionAvro action) {
        double newWeight = getWeight(action.getActionType());
        UserInteraction interaction = repository.findByUserIdAndEventId(action.getUserId(), action.getEventId())
                .orElseGet(UserInteraction::new);

        if (interaction.getId() != null && newWeight <= interaction.getWeight()) {
            return;
        }

        interaction.setUserId(action.getUserId());
        interaction.setEventId(action.getEventId());
        interaction.setWeight(newWeight);
        interaction.setTimestamp(action.getTimestamp());
        repository.save(interaction);
    }

    private double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> ActionTypeWeight.ACTION_VIEW_WEIGHT;
            case REGISTER -> ActionTypeWeight.ACTION_REGISTER_WEIGHT;
            case LIKE -> ActionTypeWeight.ACTION_LIKE_WEIGHT;
        };
    }
}