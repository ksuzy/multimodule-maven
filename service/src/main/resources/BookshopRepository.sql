CREATE TABLE author
(
    id         SERIAL PRIMARY KEY,
    firstname  VARCHAR(128) NOT NULL,
    lastname   VARCHAR(128) NOT NULL,
    patronymic VARCHAR(128),
    birthday   DATE         NOT NULL,
    UNIQUE (firstname, lastname, patronymic, birthday)
);

DROP TABLE author CASCADE;

CREATE TABLE book
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    description VARCHAR(255),
    price       NUMERIC      NOT NULL,
    quantity    SMALLINT     NOT NULL,
    issue_year  SMALLINT     NOT NULL
);

DROP TABLE book CASCADE;

CREATE TABLE author_book
(
    author_id INT    NOT NULL REFERENCES author (id),
    book_id   BIGINT NOT NULL REFERENCES book (id)
);

DROP TABLE author_book CASCADE;

CREATE TYPE status AS ENUM ('OPEN', 'CLOSED', 'PAYED', 'INPROCESS',
    'WAITINGFORPAYMENT', 'SENT', 'REJECTED', 'CANCELED');
DROP TYPE status;

CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    is_admin BOOLEAN      NOT NULL
);


DROP TABLE users CASCADE;

CREATE TABLE user_details
(
    user_id    INT PRIMARY KEY REFERENCES users (id),
    firstname  VARCHAR(128) NOT NULL,
    lastname   VARCHAR(128) NOT NULL,
    patronymic VARCHAR(128),
    phone      VARCHAR(32)
);

DROP TABLE user_details CASCADE;

CREATE TABLE user_address
(
    user_id           INT PRIMARY KEY REFERENCES user_details (user_id),
    region            VARCHAR(128) NOT NULL,
    district          VARCHAR(128) NOT NULL,
    population_center VARCHAR(128) NOT NULL,
    street            VARCHAR(128) NOT NULL,
    house             VARCHAR(128) NOT NULL,
    is_private        BOOLEAN,
    front_door        VARCHAR(128),
    floor             VARCHAR(3),
    flat              VARCHAR(5)
);

DROP TABLE user_address CASCADE;

CREATE TABLE orders
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP   NOT NULL,
    end_at     TIMESTAMP,
    status     VARCHAR(50) NOT NULL,
    price      NUMERIC     NOT NULL,
    client_id  INT REFERENCES users (id)
);

DROP TABLE orders CASCADE;

CREATE TABLE order_product
(
    order_id    INT REFERENCES orders (id),
    book_id     BIGINT REFERENCES book (id),
    quantity    INT     NOT NULL,
    total_price NUMERIC NOT NULL
);

DROP TABLE order_product CASCADE;



-- Действия в системе.
-- Ограничения:
-- 1. Пользователь не сможет оформить заказ, если на складе
-- недостаточно товаров выбранного типа
-- Действия по добавлению:
-- 1. Добавить в систему нового пользователя
-- 2. Добавить в систему новую книгу
-- 3. Добавить в систему нового автора
-- 4. Оформить заказ для выбранного пользователя: с добавлением
-- товаров/выбором их количества.
-- 5. Добавить в заказ новую книгу (и кол-во)
-- Действия по просмотру:
-- 1. Просмотреть список всех пользователей системы.
-- 2. Просмотреть список всех заказов выбранного пользователя.
-- 3. Просмотреть список всех книг в системе.
-- 4. Просмотреть список всех авторов в системе.
-- 5. Просмотреть список всех книг выбранного автора.
-- 6. Просмотреть список всех книг выбранного года выпуска.
-- 7. Просмотреть список всех заказов, в которые включен выбранный товар
-- Действия по изменению:
-- 1. Изменить статус заказа.
-- 2. Удалить заказы, сделанные раньше введённой даты.

-- Действия по добавлению:

-- 1. Добавить в систему нового пользователя first_name, second_name, patronymic,, phone, address
INSERT INTO users (email, password)
VALUES ('', '');

--1.1 Добавить в систему детали для нового пользователя
INSERT INTO user_details (firstname, lastname, patronymic, phone)
VALUES ('', '', '', '', TRUE);

--1.1 Добавить в систему адрпес для нового пользователя
INSERT INTO user_address (region, district, population_center, street, house, is_private, front_door, floor, flat)
VALUES ('', '', '', '', TRUE, '', '', '', '');

-- 2. Добавить в систему новую книгу
INSERT INTO book (name, description, price, quantity, issue_year)
VALUES ('', '', 200, 10, 1998);
INSERT INTO book (name, description, price, quantity, issue_year)
VALUES ('', 200, 10, 1998);

-- 2. Добавить в систему новую книгу
INSERT INTO book (name, description, price, quantity, issue_year)
VALUES ('', '', 200, 10, 1998);
-- 2. Добавить в систему новую книгу без описания
INSERT INTO book (name, price, quantity, issue_year)
VALUES ('', 200, 10, 1998);

-- 3. Добавить в систему нового автора
INSERT INTO author (firstname, lastname, patronymic, birthday)
VALUES ('', '', '', '1998-01-08');
-- 3. Добавить в систему нового автора без отчества
INSERT INTO author (firstname, lastname, birthday)
VALUES ('', '', '1998-01-08');

-- 4. Оформить заказ для выбранного пользователя: с добавлением
-- товаров/выбором их количества.
INSERT INTO orders (created_at, status, price, client_id)
VALUES (current_timestamp, '', 200, 2);

-- 5. Добавить в заказ новую книгу (и кол-во)
INSERT INTO order_product (order_id, book_id, quantity, total_price)
VALUES (1, 2, 3, 3 * 200);

-- Действия по просмотру:

-- 1. Просмотреть список всех пользователей системы.
SELECT id,
       firstname,
       lastname,
       patronymic,
       email,
       password,
       phone
FROM users
         JOIN user_details ON id = user_details.user_id;

-- 2. Просмотреть список всех заказов выбранного пользователя.
SELECT id, created_at, end_at, status, price, client_id
FROM orders
WHERE client_id = 1;

-- 3. Просмотреть список всех книг в системе.
SELECT id, name, description, price, quantity, issue_year
FROM book;

-- 4. Просмотреть список всех авторов в системе.
SELECT id, firstname, lastname, patronymic, birthday
FROM author;

-- 5. Просмотреть список всех книг выбранного автора.
SELECT id, name, description, price, quantity, issue_year
FROM book
WHERE (SELECT id FROM author_book WHERE author_id = 1) = id;

-- 6. Просмотреть список всех книг выбранного года выпуска.
SELECT id, name, description, price, quantity, issue_year
FROM book
WHERE issue_year = '1998';

-- Действия по изменению:

-- 1. Изменить статус заказа по айди.
UPDATE orders
SET status = '?'
WHERE id = 1;

-- 2. Удалить заказы, сделанные раньше введённой даты.
DELETE
FROM orders
WHERE end_at < '1998-01-08';