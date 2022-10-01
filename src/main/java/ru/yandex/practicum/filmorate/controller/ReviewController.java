package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController implements Controllers<Review> {

    private final ReviewService reviewService;

    @Override
    public List<Review> getAll() {
        return null;
    }

    @GetMapping
    public List<Review> getAllById(@RequestParam int filmId, @RequestParam(defaultValue = "10") int count) {
        return reviewService.getAllById(filmId, count);
    }


    @Override
    @GetMapping({"{id}"})
    public Review getById(@PathVariable("id") int reviewId) {
        return reviewService.getById(reviewId);
    }


    @Override
    @PostMapping
    public Review add(@RequestBody Review obj) {
        return reviewService.add(obj);
    }

    @Override
    @PutMapping
    public Review update(@RequestBody Review obj) {
        return reviewService.update(obj);
    }

    @PutMapping("/{id}/like/{userId}")
    public String addLikeById(@PathVariable Integer id, @PathVariable Integer userId) throws SQLException {
        return reviewService.addLikeById(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public String addDislikeById(@PathVariable Integer id, @PathVariable Integer userId) throws SQLException {
        return reviewService.addDislikeById(id, userId);
    }

    @DeleteMapping("/{id}")
    public String removeById(@PathVariable Integer id) {
        return reviewService.removeById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String removeLikeById(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.removeLikeById(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public String removeDislikeLikeById(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.removeDislikeById(id, userId);
    }
}
