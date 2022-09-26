package com.dmdev;

import com.dmdev.entity.*;
import com.dmdev.entity.fields.Role;
import com.dmdev.entity.fields.Status;
import com.dmdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Runner {

    public static void main(String[] args) {

    }

    private static void insertUserDetails() {
        UserDetails userDetails = UserDetails.builder()
                .userId(1)
                .firstname("Pavel")
                .lastname("Pavlov")
                .patronymic("Pavlovich")
                .phone("+79185554055")
                .build();
        insert(userDetails);
    }

    private static void insertUserAddress() {
        UserAddress userAddress = UserAddress.builder()
                .userId(1)
                .region("Краснодарский край")
                .district("Геленджикский район")
                .populationCenter("село Пшада")
                .street("улица Красной армии")
                .house("14")
                .isPrivate(true)
                .build();
        insert(userAddress);
    }

    private static void insertUser() {
        User user = User.builder()
                .email("IvanEmail")
                .password("IvanPass")
                .role(Role.USER)
                .build();
        insert(user);
    }

    private static void insertOrder() {
        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .status(Status.OPEN)
                .price(BigDecimal.valueOf(259.99))
                .clientId(1)
                .build();
        insert(order);
    }

    private static void insertBook() {
        Book book = Book.builder()
                .name("bookOfIvan")
                .description("sadf")
                .price(BigDecimal.valueOf(259.99))
                .quantity((short)25)
                .issueYear((short)1995)
                .build();
        insert(book);
    }

    private static void insertAuthor() {
        Author author = Author.builder()
                .firstname("IvanPersist")
                .lastname("Ivanov")
                .patronymic("Ivanovich")
                .birthday(LocalDate.of(1974, 9, 23))
                .build();

        insert(author);
    }

    private static void insert(Object obj) {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(obj);
            session.getTransaction().commit();
        }
    }
}
