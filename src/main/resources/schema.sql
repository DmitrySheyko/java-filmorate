CREATE TABLE IF NOT EXISTS genres
(
    genre_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS ratings
(
    rating_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name    VARCHAR NOT NULL,
    description  VARCHAR(200),
    release_date date    NOT NULL,
    duration     INTEGER
        CONSTRAINT film_duration_check CHECK (duration > 0),
    rating       INTEGER
        CONSTRAINT film_rating_FK REFERENCES ratings (rating_id),
    director     INTEGER

);

CREATE TABLE IF NOT EXISTS films_directors
(
    film_id  INTEGER
        CONSTRAINT film_director_film_id_FK REFERENCES films (film_id),
    director_id INTEGER
        CONSTRAINT film_director_director_id_FK REFERENCES directors (director_id)  ON DELETE CASCADE,
    CONSTRAINT films_directors_film_id_director_id_unique UNIQUE (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS films_genres
(
    film_id  INTEGER
        CONSTRAINT film_genre_film_id_FK REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER
        CONSTRAINT film_genre_genre_id_FK REFERENCES genres (genre_id),
    CONSTRAINT films_genres_film_id_genre_id_unique UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login     VARCHAR NOT NULL,
    user_name VARCHAR,
    email     VARCHAR
        CONSTRAINT user_email_check CHECK (email LIKE '%_@_%.__%'),
    birth_day DATE    NOT NULL
);

CREATE TABLE IF NOT EXISTS users_friends
(
    user_id   INTEGER
        CONSTRAINT user_friends_user_id_FK REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id INTEGER
        CONSTRAINT user_friends_friend_id_FK REFERENCES users (user_id) ON DELETE CASCADE,
--     status    INTEGER
--         CONSTRAINT user_friends_status_check CHECK (status IN (0, 1)),
    CONSTRAINT users_friends_user_id_friend_id_unique UNIQUE (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS films_likes
(
    film_id INTEGER
        CONSTRAINT film_likes_film_id_FK REFERENCES films (film_id) ON DELETE CASCADE,
    user_id INTEGER
        CONSTRAINT film_likes_user_id_FK REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT films_likes_film_id_user_id_unique UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    content VARCHAR(1000) NOT NULL,
    is_positive BOOLEAN
);

CREATE TABLE IF NOT EXISTS reviews_likes
(
    review_id INTEGER REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    is_positive INTEGER CONSTRAINT reviews_likes_value_check CHECK (is_positive=1 OR is_positive=-1),
    primary key(review_id,user_id)
);