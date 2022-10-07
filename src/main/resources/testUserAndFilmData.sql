INSERT INTO films (film_name, description, release_date, duration, rating)
VALUES ('TestFilmName1', 'TestFilmDescription1', '2011-10-11', 10, 1 );

INSERT INTO films (film_name, description, release_date, duration, rating)
VALUES ('TestFilmName2', 'TestFilmDescription2', '2012-10-12', 20, 2 );

INSERT INTO films (film_name, description, release_date, duration, rating)
VALUES ('TestFilmName3', 'TestFilmDescription3', '2013-10-13', 30, 3 );

INSERT INTO films (film_name, description, release_date, duration, rating)
VALUES ('TestFilmName4', 'TestFilmDescription4', '2014-10-14', 40, 4 );

DELETE
FROM films
WHERE film_id > 4;

INSERT INTO users (user_name, login, email, birth_day)
VALUES ('TestUserName1', 'TestUserLogin1', 'TestUserEmail1@ru.ru', '2011-10-11');

INSERT INTO users (user_name, login, email, birth_day)
VALUES ('TestUserName2', 'TestUserLogin2', 'TestUserEmail2@ru.ru', '2012-10-12');

INSERT INTO users (user_name, login, email, birth_day)
VALUES ('TestUserName3', 'TestUserLogin3', 'TestUserEmail3@ru.ru', '2013-10-13');

INSERT INTO users (user_name, login, email, birth_day)
VALUES ('TestUserName4', 'TestUserLogin4', 'TestUserEmail4@ru.ru', '2014-10-14');

DELETE
FROM users
WHERE user_id > 4;

DELETE
FROM films_genres;

DELETE
FROM films_likes;

DELETE
FROM users_friends;