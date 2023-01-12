package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class FeedService {

    public static final Integer eventTypeLike = 1;
    public static final Integer eventTypeReview = 2;
    public static final Integer eventTypeFriend = 3;
    public static final Integer operationRemove = 1;
    public static final Integer operationAdd = 2;
    public static final Integer operationUpdate = 3;

    private final FeedDbStorage feedDbStorage;
    private final UserDbStorage userDbStorage;

    public List<Feed> getByUserId(Integer userId) {
        try {
            if (!userDbStorage.checkIsObjectInStorage(userId)) {
                String message = "Пользователь user_id=" + userId + "отсутствует в базе.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            List<Feed> feeds = feedDbStorage.getByUserId(userId);
            log.info("Получен список изменений, созданных пользователем user_id=" + userId + ".");
            return feeds;
        } catch (IncorrectResultSizeDataAccessException e) {
            log.info("Пользователь user_id=" + userId + " ещё не успел внести изменения.");
            return Collections.emptyList();
        }
    }
}
