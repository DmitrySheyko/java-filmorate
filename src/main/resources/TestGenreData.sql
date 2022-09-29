INSERT INTO genres (genre_name)
VALUES ('Комедия');

INSERT INTO genres (genre_name)
VALUES ('Драма');

INSERT INTO genres (genre_name)
VALUES ('Мультфильм');

INSERT INTO genres (genre_name)
VALUES ('Документальный');

INSERT INTO genres (genre_name)
VALUES ('Вестерн');

INSERT INTO genres (genre_name)
VALUES ('Ужасы');

DELETE
FROM genres
WHERE genre_id > 6;
