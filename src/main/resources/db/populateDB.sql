DELETE FROM user_role;
DELETE FROM dish;
DELETE FROM restaurant;
DELETE FROM user;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO user (name, email, password)
VALUES ('User', 'user@gmail.com', '{noop}user'),
       ('Admin', 'admin@gmail.com', '{noop}admin');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO restaurant (name)
VALUES ('Континенталь'),
       ('Кушай хорошо');

INSERT INTO dish (date, name, price, restaurant_id)
VALUES ('2020-04-04', 'Куличики', 15.5, 100002),
       ('2020-04-04', 'Холодец', 30, 100002),
       ('2020-04-04', 'Окрошка', 31.1, 100002),
       ('2020-05-04', 'Пюре', 23.4, 100003),
       ('2020-05-04', 'Крабовье', 27, 100003),
       ('2020-05-04', 'Борщь', 28.7, 100003),
       (now(), 'Оливье', 20.2, 100002),
       (now(), 'Макароны по флотски', 25, 100002),
       (now(), 'Россольник', 28.2, 100002),
       (now(), 'Карпачо', 35, 100002),
       (now(), 'Фуагра', 40.4, 100003),
       (now(), 'Карпачо', 38.7, 100003),
       (now(), 'Лазанья', 45, 100003);

INSERT INTO vote (date, user_id, restaurant_id)
VALUES ('2020-04-04', 100000, 100003),
       ('2020-04-04', 100001, 100003),
       ('2020-04-05', 100000, 100002),
       ('2020-04-05', 100001, 100003);
