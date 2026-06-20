package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto registerUser(NewUserRequest newUser) {
        return userMapper.mapToUserDto(userRepository.save(userMapper.mapToUser(newUser)));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);

        if (ids != null && !ids.isEmpty()) {
            return userRepository.findAll(QUser.user.id.in(ids), page).stream()
                    .map(userMapper::mapToUserDto)
                    .toList();
        }

        return userRepository.findAll(page).stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userRepository.deleteById(userId);
    }

    @Override
    public UserShortDto getUserShortInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return userMapper.toShortDto(user);
    }

    @Override
    public List<UserShortDto> getUserShortInfo(Set<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream().map(userMapper::toShortDto).toList();
    }

    @Override
    public Map<Long, UserShortDto> userIdToUserShortDtoMap(Set<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .map(userMapper::toShortDto)
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));
    }
}
