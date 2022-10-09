package com.dmdev.util;

import com.dmdev.entity.Author;
import com.dmdev.entity.Book;
import com.dmdev.entity.Order;
import com.dmdev.entity.OrderProduct;
import com.dmdev.entity.User;
import com.dmdev.entity.UserAddress;
import com.dmdev.entity.UserDetails;
import com.dmdev.entity.fields.Role;
import com.dmdev.entity.fields.Status;
import com.dmdev.entity.fields.Role;
import com.dmdev.entity.fields.Status;

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class HibernateTestUtil {

    public static SessionFactory sessionFactory;

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14");
    static {
        container.start();
    }

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = HibernateUtil.buildConfiguration();
        configuration.setProperty("hibernate.connection.url", container.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", container.getUsername());
        configuration.setProperty("hibernate.connection.password", container.getPassword());
        configuration.configure();

        return configuration.buildSessionFactory();
    }

    public static Author createAuthorToInsert() {
        return createAuthor("IvanInsert");
    }

    public static Author createAuthorToReadUpdateDelete() {
        return createAuthor("IvanDeleteReadUpdate");
    }

    private static Author createAuthor(String firstname) {
        return Author.builder()
                .firstname(firstname)
                .lastname("Ivanov")
                .patronymic("Ivanovich")
                .birthday(LocalDate.of(1974, 9, 23))
                .build();
    }

    public static Book createBook(Author author) {
        Book book = Book.builder()
                .name("Chipolino")
                .description("sadf")
                .price(BigDecimal.valueOf(259.99))
                .quantity((short) 25)
                .issueYear((short) 1995)
                .build();
        book.addAuthor(author);
        return book;
    }

    public static User createUserToInsert() {
        return createUser("UserInsert");
    }

    public static User createUserToReadUpdateDelete() {
        return createUser("UserDeleteReadeUpdate");
    }

    private static User createUser(String email) {
        return User.builder()
                .email(email)
                .password("IvanPass")
                .role(Role.USER)
                .build();
    }

    public static Order createOrder() {
        return Order.builder()
                .createdAt(LocalDateTime.of(
                        2022,
                        9,
                        25,
                        22,
                        38,
                        02
                ))
                .status(Status.OPEN)
                .price(BigDecimal.valueOf(259.99))
                .build();
    }

    public static UserDetails createUserDetails() {
        return UserDetails.builder()
                .firstname("Pavel")
                .lastname("Pavlov")
                .patronymic("Pavlovich")
                .phone("+79185554055")
                .build();
    }

    public static UserAddress createUserAddress() {
        UserAddress address = UserAddress.builder()
                .region("Краснодарский край")
                .district("Геленджикский район")
                .populationCenter("село Пшада")
                .street("улица Красной армии")
                .house("14")
                .isPrivate(true)
                .build();
        return address;
    }

    public static OrderProduct createOrderProduct() {
        int quantity = 5;
        return OrderProduct.builder()
                .quantity(quantity)
                .totalPrice(BigDecimal.valueOf(259.99 * quantity))
                .build();
    }
}
