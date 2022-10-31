package com.dmdev.util;

import com.dmdev.database.entity.Author;
import com.dmdev.database.entity.BaseEntity;
import com.dmdev.database.entity.Book;
import com.dmdev.database.entity.Order;
import com.dmdev.database.entity.OrderProduct;
import com.dmdev.database.entity.User;
import com.dmdev.database.entity.UserAddress;
import com.dmdev.database.entity.UserDetails;
import com.dmdev.database.entity.fields.Role;
import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TestDataImporter {

    public List<BaseEntity> importData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        List<BaseEntity> result = new ArrayList<>();

        session.beginTransaction();

        Author author = saveAuthor(session, "Кей", "Хорстманн");
        Author author1 = saveAuthor(session, "Гари", "Корнелл");
        Author author2 = saveAuthor(session, "Стивен", "Кови");
        Author author3 = saveAuthor(session, "Тони", "Роббинс");
        Author author4 = saveAuthor(session, "Наполеон", "Хилл");
        Author author5 = saveAuthor(session, "Роберт", "Кийосаки");
        Author author6 = saveAuthor(session, "Дейл", "Карнеги");
        session.flush();

        result.add(author);
        result.add(author1);
        result.add(author2);
        result.add(author3);
        result.add(author4);
        result.add(author5);
        result.add(author6);

        Book book = saveBook(session, "Java. Библиотека профессионала. Том 1", (short) 2010, BigDecimal.valueOf(1102.44), List.of(author, author1));
        Book book1 = saveBook(session, "Java. Библиотека профессионала. Том 2", (short) 2012, BigDecimal.valueOf(954.22), List.of(author, author1));
        Book book2 = saveBook(session, "Java SE 8. Вводный курс", (short) 2015, BigDecimal.valueOf(203.83), List.of(author));
        Book book3 = saveBook(session, "7 навыков высокоэффективных людей", (short) 1989, BigDecimal.valueOf(396.13), List.of(author2));
        Book book4 = saveBook(session, "Разбуди в себе исполина", (short) 1991, BigDecimal.valueOf(576.00), List.of(author3));
        Book book5 = saveBook(session, "Думай и богатей", (short) 1937, BigDecimal.valueOf(336.70), List.of(author4));
        Book book6 = saveBook(session, "Богатый папа, бедный папа", (short) 1997, BigDecimal.valueOf(352.88), List.of(author5));
        Book book7 = saveBook(session, "Квадрант денежного потока", (short) 1998, BigDecimal.valueOf(368.99), List.of(author5));
        Book book8 = saveBook(session, "Как перестать беспокоиться и начать жить", (short) 1948, BigDecimal.valueOf(368.00), List.of(author6));
        Book book9 = saveBook(session, "Как завоевывать друзей и оказывать влияние на людей", (short) 1936, BigDecimal.valueOf(352.00), List.of(author6));

        result.add(book);
        result.add(book1);
        result.add(book2);
        result.add(book3);
        result.add(book4);
        result.add(book5);
        result.add(book6);
        result.add(book7);
        result.add(book8);
        result.add(book9);

        User billGates = saveUserAndUserAddress(session, "Bill", "Gates");
        User steveJobs = saveUserAndUserAddress(session, "Steve", "Jobs");
        User sergeyBrin = saveUserAndUserAddress(session, "Sergey", "Brin");

        result.add(billGates);
        result.add(steveJobs);
        result.add(sergeyBrin);

        result.add(saveUserDetails(session, billGates, "Bill", "Gates"));
        result.add(saveUserDetails(session, steveJobs, "Steve", "Jobs"));
        result.add(saveUserDetails(session, sergeyBrin, "Sergey", "Brin"));


        result.add(saveUserAddress(session, billGates, "14A"));
        result.add(saveUserAddress(session, steveJobs, "15"));
        result.add(saveUserAddress(session, sergeyBrin, "15K4"));

        Order order = saveOrder(session, billGates);
        Order order1 = saveOrder(session, billGates);
        Order order2 = saveOrder(session, billGates);
        Order order3 = saveOrder(session, sergeyBrin);

        result.add(order);
        result.add(order1);
        result.add(order2);
        result.add(order3);

        result.add(saveOrderProduct(session, order, book, 25));
        result.add(saveOrderProduct(session, order, book1, 25));
        result.add(saveOrderProduct(session, order1, book6, 25));
        result.add(saveOrderProduct(session, order1, book7, 25));
        result.add(saveOrderProduct(session, order1, book5, 25));
        result.add(saveOrderProduct(session, order2, book4, 15));
        result.add(saveOrderProduct(session, order3, book8, 25));
        result.add(saveOrderProduct(session, order3, book9, 25));
        result.add(saveOrderProduct(session, order3, book2, 25));
        result.add(saveOrderProduct(session, order3, book3, 25));
        result.add(saveOrderProduct(session, order3, book4, 10));

        session.getTransaction().commit();
        return result;
    }

    private OrderProduct saveOrderProduct(Session session, Order order, Book book, int quantity) {
        OrderProduct orderProduct = HibernateTestUtil.createOrderProduct(quantity, book.getPrice());
        order.addOrderProduct(orderProduct);
        book.addOrderProduct(orderProduct);
        session.save(orderProduct);
        return orderProduct;
    }

    private UserAddress saveUserAddress(Session session, User user, String house) {
        UserAddress userAddress = HibernateTestUtil.createUserAddress(house);
        user.addUserAddress(userAddress);
        session.save(userAddress);
        return userAddress;
    }

    private UserDetails saveUserDetails(Session session, User user, String firstname, String lastname) {
        UserDetails userDetails = HibernateTestUtil.createUserDetails(firstname, lastname);
        user.addUserDetails(userDetails);
        session.save(userDetails);
        return userDetails;
    }

    private Order saveOrder(Session session, User user) {
        Order order = HibernateTestUtil.createOrder(0.0);
        user.addOrder(order);
        session.save(order);
        return order;
    }

    private Author saveAuthor(Session session, String firstname, String lastname) {
        Author author = HibernateTestUtil.createAuthor(firstname, lastname);
        session.save(author);
        return author;
    }

    private Book saveBook(Session session, String name, short issueYear, BigDecimal price, List<Author> authors) {
        Book book = HibernateTestUtil.createBook(name, issueYear, price, authors);
        session.save(book);
        return book;
    }

    private User saveUserAndUserAddress(Session session,
                                        String firstName,
                                        String lastName) {
        User user = HibernateTestUtil.createUser(firstName + lastName + "@Email.com", "pass", Role.ADMIN);
        session.save(user);
        return user;
    }
}
