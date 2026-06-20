package ru.practicum.client.config.errordecoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.InternalServerException;
import ru.practicum.exception.NotFoundException;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == 404) {
            log.error("Ошибка при вызове метода: {}, ответ: {} ", s, response);
            return new NotFoundException("Запрашиваемый ресурс не найден: " + response.request().url());
        }

        if (response.status() >= 400 && response.status() <= 499) {
            log.error("Ошибка при выполнении запроса: {}, получен ответ: {} ", s, response);
            return new ConditionsNotMetException("Некорректный запрос: " + s);
        }

        if (response.status() >= 500 && response.status() <= 599) {
            log.error("Ошибка при вызове метода: {}, ответ: {} ", s, response);
            return new InternalServerException("Сервис временно недоступен");
        }

        return defaultDecoder.decode(s, response);
    }
}