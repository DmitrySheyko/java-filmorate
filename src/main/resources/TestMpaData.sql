INSERT INTO ratings (rating_name)
VALUES ('G');

INSERT INTO ratings (rating_name)
VALUES ('PG');

INSERT INTO ratings (rating_name)
VALUES ('PG-13');

INSERT INTO ratings (rating_name)
VALUES ('R');

INSERT INTO ratings (rating_name)
VALUES ('NC-17');

DELETE
FROM ratings
WHERE rating_id > 5;