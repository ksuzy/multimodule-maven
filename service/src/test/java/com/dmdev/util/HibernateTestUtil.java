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

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class HibernateTestUtil {

    public static SessionFactory sessionFactory;

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14");
    static {
        container.start();
    }

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = HibernateUtil.buildConfiguration()
                .setProperty("hibernate.connection.url", container.getJdbcUrl())
                .setProperty("hibernate.connection.username", container.getUsername())
                .setProperty("hibernate.connection.password", container.getPassword())
                .configure();

        return configuration.buildSessionFactory();
    }

    public static Author createAuthorToInsert() {
        return createAuthor("IvanInsert");
    }

    public static Author createAuthorToReadUpdateDelete() {
        return createAuthor("IvanDeleteReadUpdate");
    }

    public static Author createAuthor(String firstname) {
        return createAuthor(firstname, "Ivanov");
    }

    public static Author createAuthor(String firstname, String lastname) {
        return Author.builder()
                .firstname(firstname)
                .lastname(lastname)
                .patronymic("Ivanovich")
                .birthday(LocalDate.of(1974, 9, 23))
                .build();
    }

    public static Book createBook(Author author) {
        return createBook("Chipolino", (short) 1998, BigDecimal.valueOf(259.99), List.of(author));
    }

    public static Book createBook(String name, Short issueYear, BigDecimal price, List<Author> authors) {
        Book book = Book.builder()
                .name(name)
                .description("sadf")
                .price(price)
                .quantity((short) 25)
                .issueYear(issueYear)
                .build();
        authors.forEach(book::addAuthor);
        return book;
    }

    public static User createUserToInsert() {
        return createUser("UserInsert");
    }

    public static User createUserToReadUpdateDelete() {
        return createUser("UserDeleteReadeUpdate");
    }

    private static User createUser(String email) {
        return createUser(email, "IvanPass", Role.USER);
    }

    public static User createUser(String email, String pass, Role role) {
        return User.builder()
                .email(email)
                .password(pass)
                .role(role)
                .build();
    }

    public static Order createOrder(double price) {
        return Order.builder()
                .createdAt(LocalDateTime.now())
                .status(Status.OPEN)
                .price(BigDecimal.valueOf(price))
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
                        2
                ))
                .status(Status.OPEN)
                .price(BigDecimal.valueOf(259.99))
                .build();
    }

    public static UserDetails createUserDetails(String firstname, String lastname) {
        return UserDetails.builder()
                .firstname(firstname)
                .lastname(lastname)
                .patronymic("Pavlovich")
                .phone("+79185554055")
                .build();
    }

    public static UserDetails createUserDetails() {
        return createUserDetails("Pavel", "Pavlov");
    }

    public static UserAddress createUserAddress() {
        return createUserAddress("14");
    }

    public static UserAddress createUserAddress(String house) {
        return UserAddress.builder()
                .region("Краснодарский край")
                .district("Геленджикский район")
                .populationCenter("село Пшада")
                .street("улица Красной армии")
                .house(house)
                .isPrivate(true)
                .build();
    }

    public static OrderProduct createOrderProduct(int quantity, BigDecimal priceOfBook) {
        return OrderProduct.builder()
                .quantity(quantity)
                .totalPrice(priceOfBook.multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    public static OrderProduct createOrderProduct() {
        int quantity = 5;
        return createOrderProduct(quantity, BigDecimal.valueOf(259.99));
    }
}