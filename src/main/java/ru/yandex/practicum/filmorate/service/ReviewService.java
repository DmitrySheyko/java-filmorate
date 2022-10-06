package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class ReviewService implements Services<Review> {

    private final ReviewDbStorage reviewDbStorage;
    private final FeedDbStorage feedDbStorage;

    @Override
    public List<Review> getAll() {
        try {
            List<Review> reviews = reviewDbStorage.getAll();
            log.info("Получен список всех отзывов");
            return reviews;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<Review> getAllById(Integer filmId, Integer count) {
        try {
            if (!reviewDbStorage.checkIsObjectInStorage(filmId)) {
                String message = "Фильм film_id=" + filmId + "отсутствует в базе.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            List<Review> reviews = reviewDbStorage.getAllById(filmId, count);
            log.info("Получен список всех отзывов");
            return reviews;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Review getById(int id) {
        try {
            Review review = reviewDbStorage.getById(id);
            log.info("Получен отзыв review_id=" + id + ".");
            return review;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Отзыв review_id=" + id + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    @Override
    public Review add(Review newReview) {
        try {//может возникнуть ошибка в случае добавления отзыва с нарушением уникальности пары film_id <-> user_id
            if (!reviewDbStorage.checkIsUserInStorage(newReview.getUserId())) {
                String message = "Пользователь user_id=" + newReview.getUserId() + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            if (!reviewDbStorage.checkIsObjectInStorage(newReview.getFilmId())) {
                String message = "Фильм film_id=" + newReview.getFilmId() + " отсутствует в базе данных.";
                log.error(message);
                throw new ObjectNotFoundException(message);
            }
            reviewDbStorage.add(newReview);
            log.info("Отзыв review_id=" + newReview.getReviewId() + " успешно добавлен.");
            feedDbStorage.add(newReview.getReviewId(),
                    FeedService.eventTypeReview, FeedService.operationAdd, newReview.getUserId());
            log.info("Лента событий пользователя user_id=" + newReview.getUserId() + " была обновлена.");
            return newReview;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Отзыв пользователем user_id=" + newReview.getUserId() + " фильму film_id="
                    + newReview.getFilmId() + " уже был оставлен.";
            log.error(message);
            throw new ValidationException(message);
        }
    }

    @Override
    public Review update(Review reviewForUpdate) {
        try {
            reviewDbStorage.update(reviewForUpdate);
            log.info("Отзыв review_id=" + reviewForUpdate.getReviewId() + " успешно обновлен.");
            Review review = getById(reviewForUpdate.getReviewId());
            feedDbStorage.add(review.getReviewId(),
                    FeedService.operationAdd, FeedService.operationUpdate, review.getUserId());
            log.info("Лента событий пользователя user_id=" + reviewForUpdate.getUserId() + " была обновлена.");
            return review;
        } catch (IncorrectResultSizeDataAccessException e) {
            String message = "Отзыв review_id=" + reviewForUpdate.getFilmId() + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
    }

    public String addLikeById(Integer reviewId, Integer userId) throws SQLException {
        if (!reviewDbStorage.checkIsUserInStorage(userId)) {
            String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (reviewDbStorage.checkIsReviewLikedByUser(reviewId, userId)) {
            String message = "Пользователь user_id=" + userId + " уже поставил лайк отзыву review_id= " +
                    reviewId + ".";
            log.error(message);
            throw new SQLException(message);
        }
        if (reviewDbStorage.checkIsReviewDislikedByUser(reviewId, userId)) {
            removeDislikeById(reviewId, userId);
        }
        String message = reviewDbStorage.addLikeOrDislikeById(reviewId, userId, 1);
        log.info(message);
        return message;
    }

    public String removeLikeById(Integer reviewId, Integer userId) {
        if (!reviewDbStorage.checkIsUserInStorage(userId)) {
            String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        String message = reviewDbStorage.removeLikeOrDislikeById(reviewId, userId, 1);
        log.info(message);
        return message;
    }

    public String addDislikeById(Integer reviewId, Integer userId) throws SQLException {
        if (!reviewDbStorage.checkIsUserInStorage(userId)) {
            String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        if (reviewDbStorage.checkIsReviewDislikedByUser(reviewId, userId)) {
            String message = "Пользователь user_id=" + userId + " уже поставил дизлайк отзыву review_id= " +
                    reviewId + ".";
            log.error(message);
            throw new SQLException(message);
        }
        if (reviewDbStorage.checkIsReviewLikedByUser(reviewId, userId)) {
            removeLikeById(reviewId, userId);
        }
        String message = reviewDbStorage.addLikeOrDislikeById(reviewId, userId, -1);
        log.info(message);
        return message;
    }

    public String removeDislikeById(Integer reviewId, Integer userId) {
        if (!reviewDbStorage.checkIsUserInStorage(userId)) {
            String message = "Пользователь user_id=" + userId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        String message = reviewDbStorage.removeLikeOrDislikeById(reviewId, userId, -1);
        log.info(message);
        return message;
    }

    public String removeById(Integer reviewId) {
        if (!reviewDbStorage.checkIsReviewInStorage(reviewId)) {
            String message = "Отзыв review_id=" + reviewId + " отсутствует в базе данных.";
            log.error(message);
            throw new ObjectNotFoundException(message);
        }
        Review review = getById(reviewId);
        String message = reviewDbStorage.removeById(reviewId);
        log.info(message);
        feedDbStorage.add(review.getReviewId(),
                FeedService.eventTypeReview, FeedService.operationRemove, review.getUserId());
        log.info("Лента событий пользователя user_id=" + review.getUserId() + " была обновлена.");
        return message;
    }
}
