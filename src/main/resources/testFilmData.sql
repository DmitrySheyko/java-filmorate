DELETE
FROM FILMS;

INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING)
VALUES ('TestFilmName1', 'TestFilmDescription1', '2011-10-11', 10, 1 );

INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING)
VALUES ('TestFilmName2', 'TestFilmDescription2', '2012-10-12', 20, 2 );

INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING)
VALUES ('TestFilmName3', 'TestFilmDescription3', '2013-10-13', 30, 3 );

INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING)
VALUES ('TestFilmName4', 'TestFilmDescription4', '2014-10-14', 40, 4 );

DELETE
FROM USERS;

INSERT INTO USERS (USER_NAME, LOGIN, EMAIL, BIRTH_DAY)
VALUES ('TestUserName1', 'TestUserLogin1', 'TestUserEmail1@ru.ru', '2011-10-11');

INSERT INTO USERS (USER_NAME, LOGIN, EMAIL, BIRTH_DAY)
VALUES ('TestUserName2', 'TestUserLogin2', 'TestUserEmail2@ru.ru', '2012-10-12');

DELETE
FROM FILMS_GENRES;

DELETE
FROM FILMS_LIKES;

DELETE
FROM USERS_FRIENDS;