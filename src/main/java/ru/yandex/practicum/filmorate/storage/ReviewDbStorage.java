package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class ReviewDbStorage implements Storages<Review> {

    private final ReviewMapper reviewMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getAll() {
        String sqlQuery = "SELECT r.review_id," +
                "r.film_id," +
                "r.user_id," +
                "r.content," +
                "r.is_positive," +
                "sum((COALESCE(rl.is_positive,0))) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON rl.review_id = r.review_id " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC,r.review_id ASC;";
        return jdbcTemplate.query(sqlQuery, reviewMapper);
    }

    public List<Review> getAllById(Integer filmId, Integer count) {
        String sqlQuery = "SELECT r.review_id," +
                "r.film_id," +
                "r.user_id," +
                "r.content," +
                "r.is_positive," +
                "sum((COALESCE(rl.is_positive,0))) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON rl.review_id = r.review_id " +
                "WHERE r.film_id=? " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC,r.review_id ASC " +
                "LIMIT " + count + ";";
        return jdbcTemplate.query(sqlQuery, reviewMapper, filmId);
    }


    @Override
    public Review getById(int id) {
        String sqlQuery = "SELECT r.review_id," +
                "r.film_id," +
                "r.user_id," +
                "r.content," +
                "r.is_positive," +
                "sum((COALESCE(rl.is_positive,0))) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON rl.review_id = r.review_id " +
                "WHERE r.review_id=?" +
                "GROUP BY r.review_id";
        return jdbcTemplate.queryForObject(sqlQuery, reviewMapper, id);
    }

    @Override
    public Review add(Review newReview) {
        String sqlQuery = "INSERT INTO reviews (film_id,user_id,content,is_positive) VALUES (?,?,?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setInt(1, newReview.getFilmId());
            stmt.setInt(2, newReview.getUserId());
            stmt.setString(3, newReview.getContent());
            stmt.setBoolean(4, newReview.getIsPositive());
            return stmt;
        }, keyHolder);
        newReview.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        newReview.setUseful(0);
        return newReview;
    }

    @Override
    public Review update(Review reviewForUpdate) {
        //обновить user_id и film_id у отзыва нельзя, в случае попытки - не уверен,нужно ли выбрасывать исключение.
        String sqlQuery = "UPDATE reviews SET content=?,is_positive=? WHERE review_id=?;";
        jdbcTemplate.update(sqlQuery, reviewForUpdate.getContent(),
                reviewForUpdate.getIsPositive(), reviewForUpdate.getReviewId());
        return reviewForUpdate;
    }

    public String removeById(Integer reviewId) {
        String sqlQuery = "DELETE FROM reviews WHERE review_id=?";
        jdbcTemplate.update(sqlQuery, reviewId);
        return "Отзыв review_id=" + reviewId + " был успешно удалён.";
    }

    public String addLikeOrDislikeById(Integer reviewId, Integer userId,int isLike) {
        String sqlQuery = "INSERT INTO reviews_likes (review_id,user_id,is_positive) VALUES (?,?,?);";
        jdbcTemplate.update(sqlQuery, reviewId, userId, isLike);
        if (isLike==1){
            return "Пользователь user_id=" + userId + " поставил лайк отзыву review_id=" + reviewId + ".";
        } else
            return "Пользователь user_id=" + userId + " поставил дизлайк отзыву review_id=" + reviewId + ".";

    }

    public String removeLikeOrDislikeById(Integer reviewId, Integer userId,int isLike) {
        String sqlQuery = "DELETE FROM reviews_likes WHERE review_id=? AND user_id=? AND is_positive=isLike;";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
        if (isLike==1){
            return "Пользователь user_id=" + userId + " убрал лайк с отзыва review_id=" + reviewId + ".";
        } else
            return "Пользователь user_id=" + userId + " убрал дизлайк с отзыва review_id=" + reviewId + ".";
    }

    @Override
    public boolean checkIsObjectInStorage(int filmId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId));
    }

    @Override
    public boolean checkIsObjectInStorage(Review review) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM review WHERE review_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, review.getReviewId()));
    }

    public boolean checkIsReviewLikedByUser(int reviewIn, int userId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM reviews_likes WHERE review_id = ? AND user_id=? " +
                "AND is_positive=1)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, reviewIn, userId));
    }

    public boolean checkIsReviewDislikedByUser(int reviewIn, int userId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM reviews_likes WHERE review_id = ? AND user_id=? " +
                "AND is_positive=-1)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, reviewIn, userId));
    }

    public boolean checkIsReviewInStorage(int reviewId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM reviews WHERE review_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, reviewId));
    }

    public boolean checkIsUserInStorage(int userId) {
        String sqlQuery = "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, userId));
    }
}
