CREATE TABLE author
(
    id         SERIAL PRIMARY KEY,
    firstname  VARCHAR(128) NOT NULL,
    lastname   VARCHAR(128) NOT NULL,
    patronymic VARCHAR(128),
    birthday   DATE         NOT NULL,
    UNIQUE (firstname, lastname, patronymic, birthday)
);

CREATE TABLE book
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    description VARCHAR(255),
    price       NUMERIC      NOT NULL,
    quantity    SMALLINT     NOT NULL,
    issue_year  SMALLINT     NOT NULL
);

create TABLE book_author
(
    author_id INT    NOT NULL REFERENCES author (id) ON delete CASCADE,
    book_id   BIGINT NOT NULL REFERENCES book (id) ON delete CASCADE,
    PRIMARY KEY (book_id, author_id)
);

create TABLE users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    role     VARCHAR(32)  NOT NULL
);

create TABLE user_details
(
    id         SERIAL PRIMARY KEY,
    user_id    INT UNIQUE   NOT NULL REFERENCES users (id),
    firstname  VARCHAR(128) NOT NULL,
    lastname   VARCHAR(128) NOT NULL,
    patronymic VARCHAR(128),
    phone      VARCHAR(32)
);

create TABLE user_address
(
    user_id           INT PRIMARY KEY REFERENCES users (id),
    region            VARCHAR(128) NOT NULL,
    district          VARCHAR(128) NOT NULL,
    population_center VARCHAR(128) NOT NULL,
    street            VARCHAR(128) NOT NULL,
    house             VARCHAR(128) NOT NULL,
    is_private        BOOLEAN,
    front_door        INT,
    floor             INT,
    flat              INT
);

create TABLE orders
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP UNIQUE NOT NULL,
    end_at     TIMESTAMP,
    status     VARCHAR(50)      NOT NULL,
    price      NUMERIC          NOT NULL,
    client_id  INT              NOT NULL REFERENCES users (id) ON delete CASCADE
);

create TABLE order_product
(
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT  NOT NULL REFERENCES orders (id),
    book_id     BIGINT  NOT NULL REFERENCES book (id),
    quantity    INT     NOT NULL,
    total_price NUMERIC NOT NULL
);



-- ???????????????? ?? ??????????????.
-- ??????????????????????:
-- 1. ???????????????????????? ???? ???????????? ???????????????? ??????????, ???????? ???? ????????????
-- ???????????????????????? ?????????????? ???????????????????? ????????
-- ???????????????? ???? ????????????????????:
-- 1. ???????????????? ?? ?????????????? ???????????? ????????????????????????
-- 2. ???????????????? ?? ?????????????? ?????????? ??????????
-- 3. ???????????????? ?? ?????????????? ???????????? ????????????
-- 4. ???????????????? ?????????? ?????? ???????????????????? ????????????????????????: ?? ??????????????????????
-- ??????????????/?????????????? ???? ????????????????????.
-- 5. ???????????????? ?? ?????????? ?????????? ?????????? (?? ??????-????)
-- ???????????????? ???? ??????????????????:
-- 1. ?????????????????????? ???????????? ???????? ?????????????????????????? ??????????????.
-- 2. ?????????????????????? ???????????? ???????? ?????????????? ???????????????????? ????????????????????????.
-- 3. ?????????????????????? ???????????? ???????? ???????? ?? ??????????????.
-- 4. ?????????????????????? ???????????? ???????? ?????????????? ?? ??????????????.
-- 5. ?????????????????????? ???????????? ???????? ???????? ???????????????????? ????????????.
-- 6. ?????????????????????? ???????????? ???????? ???????? ???????????????????? ???????? ??????????????.
-- 7. ?????????????????????? ???????????? ???????? ??????????????, ?? ?????????????? ?????????????? ?????????????????? ??????????
-- ???????????????? ???? ??????????????????:
-- 1. ???????????????? ???????????? ????????????.
-- 2. ?????????????? ????????????, ?????????????????? ???????????? ?????????????????? ????????.

-- ???????????????? ???? ????????????????????:

-- 1. ???????????????? ?? ?????????????? ???????????? ???????????????????????? first_name, second_name, patronymic,, phone, address
INSERT INTO users (email, password)
VALUES ('', '');

--1.1 ???????????????? ?? ?????????????? ???????????? ?????? ???????????? ????????????????????????
INSERT INTO user_details (firstname, lastname, patronymic, phone)
VALUES ('', '', '', '', TRUE);

--1.1 ???????????????? ?? ?????????????? ???????????? ?????? ???????????? ????????????????????????
INSERT INTO user_address (region, district, population_center, street, house, is_private, front_door, floor, flat)
VALUES ('', '', '', '', TRUE, '', '', '', '');

-- 2. ???????????????? ?? ?????????????? ?????????? ??????????
INSERT INTO book (name, description, price, quantity, issue_year)
VALUES ('', '', 200, 10, 1998);
INSERT INTO book (name, description, price, quantity, issue_year)
VALUES ('', 200, 10, 1998);

-- 2. ???????????????? ?? ?????????????? ?????????? ??????????
INSERT INTO book (name, description, price, quantity, issue_year)
VALUES ('', '', 200, 10, 1998);
-- 2. ???????????????? ?? ?????????????? ?????????? ?????????? ?????? ????????????????
INSERT INTO book (name, price, quantity, issue_year)
VALUES ('', 200, 10, 1998);

-- 3. ???????????????? ?? ?????????????? ???????????? ????????????
INSERT INTO author (firstname, lastname, patronymic, birthday)
VALUES ('', '', '', '1998-01-08');
-- 3. ???????????????? ?? ?????????????? ???????????? ???????????? ?????? ????????????????
INSERT INTO author (firstname, lastname, birthday)
VALUES ('', '', '1998-01-08');

-- 4. ???????????????? ?????????? ?????? ???????????????????? ????????????????????????: ?? ??????????????????????
-- ??????????????/?????????????? ???? ????????????????????.
INSERT INTO orders (created_at, status, price, client_id)
VALUES (current_timestamp, '', 200, 2);

-- 5. ???????????????? ?? ?????????? ?????????? ?????????? (?? ??????-????)
INSERT INTO order_product (order_id, book_id, quantity, total_price)
VALUES (1, 2, 3, 3 * 200);

-- ???????????????? ???? ??????????????????:

-- 1. ?????????????????????? ???????????? ???????? ?????????????????????????? ??????????????.
SELECT id,
       firstname,
       lastname,
       patronymic,
       email,
       password,
       phone
FROM users
         JOIN user_details ON id = user_details.user_id;

-- 2. ?????????????????????? ???????????? ???????? ?????????????? ???????????????????? ????????????????????????.
SELECT id, created_at, end_at, status, price, client_id
FROM orders
WHERE client_id = 1;

-- 3. ?????????????????????? ???????????? ???????? ???????? ?? ??????????????.
SELECT id, name, description, price, quantity, issue_year
FROM book;

-- 4. ?????????????????????? ???????????? ???????? ?????????????? ?? ??????????????.
SELECT id, firstname, lastname, patronymic, birthday
FROM author;

-- 5. ?????????????????????? ???????????? ???????? ???????? ???????????????????? ????????????.
SELECT id, name, description, price, quantity, issue_year
FROM book
WHERE (SELECT id FROM author_book WHERE author_id = 1) = id;

-- 6. ?????????????????????? ???????????? ???????? ???????? ???????????????????? ???????? ??????????????.
SELECT id, name, description, price, quantity, issue_year
FROM book
WHERE issue_year = '1998';

-- ???????????????? ???? ??????????????????:

-- 1. ???????????????? ???????????? ???????????? ???? ????????.
UPDATE orders
SET status = '?'
WHERE id = 1;

-- 2. ?????????????? ????????????, ?????????????????? ???????????? ?????????????????? ????????.
DELETE
FROM orders
WHERE end_at < '1998-01-08';