INSERT INTO ratings (rating_name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO genres (genre_name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Документальный'),
       ('Вестерн'),
       ('Ужасы');

INSERT INTO event_types (event_type)
VALUES ('LIKE'),
       ('REVIEW'),
       ('FRIEND');

INSERT INTO operations (operation_type)
VALUES ('REMOVE'),
       ('ADD'),
       ('UPDATE');