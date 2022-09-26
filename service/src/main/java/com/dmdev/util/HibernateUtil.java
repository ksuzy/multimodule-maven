package com.dmdev.util;

import com.dmdev.converter.RoleConverter;
import com.dmdev.converter.StatusConverter;
import com.dmdev.entity.*;
import com.dmdev.entity.fields.Role;
import com.dmdev.entity.fields.Status;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class HibernateUtil {

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.addAnnotatedClass(Author.class);
        configuration.addAnnotatedClass(Book.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(UserDetails.class);
        configuration.addAnnotatedClass(UserAddress.class);
        configuration.addAttributeConverter(StatusConverter.class);
        configuration.addAttributeConverter(RoleConverter.class);
        configuration.configure();

        return configuration.buildSessionFactory();
    }

    public static Author createAuthorToInsert() {
        return createAuthor("IvanInsert");
    }

    public static Author createAuthorToReadUpdateDelete() {
        return createAuthor("IvanDeleteReadeUpdate");
    }

    private static Author createAuthor(String firstname) {
        return Author.builder()
                .firstname(firstname)
                .lastname("Ivanov")
                .patronymic("Ivanovich")
                .birthday(LocalDate.of(1974, 9, 23))
                .build();
    }

    public static Book createBookToInsert() {
        return createBook("bookInsert");
    }

    public static Book createBookToReadUpdateDelete() {
        return createBook("bookDeleteReadeUpdate");
    }

    private static Book createBook(String name) {
        return Book.builder()
                .name(name)
                .description("sadf")
                .price(BigDecimal.valueOf(259.99))
                .quantity((short) 25)
                .issueYear((short) 1995)
                .build();
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

    public static Order createOrder(Integer clientId) {
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
                .clientId(clientId)
                .build();
    }

    public static UserDetails createUserDetails(Integer userId) {
        return UserDetails.builder()
                .userId(userId)
                .firstname("Pavel")
                .lastname("Pavlov")
                .patronymic("Pavlovich")
                .phone("+79185554055")
                .build();
    }

    public static UserAddress createUserAddress(Integer userId) {
        return UserAddress.builder()
                .userId(userId)
                .region("Краснодарский край")
                .district("Геленджикский район")
                .populationCenter("село Пшада")
                .street("улица Красной армии")
                .house("14")
                .isPrivate(true)
                .build();
    }
}
