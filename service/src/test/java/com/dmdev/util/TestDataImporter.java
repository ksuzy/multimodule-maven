package com.dmdev.util;

import com.dmdev.entity.Author;
import com.dmdev.entity.Book;
import com.dmdev.entity.Order;
import com.dmdev.entity.OrderProduct;
import com.dmdev.entity.User;
import com.dmdev.entity.UserAddress;
import com.dmdev.entity.UserDetails;
import com.dmdev.entity.fields.Role;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class TestDataImporter {

    public void importData(SessionFactory sessionFactory) {
        @Cleanup Session session = sessionFactory.openSession();

        Author author = saveAuthor(session, "Кей", "Хорстманн");
        Author author6 = saveAuthor(session, "Гари", "Корнелл");
        Author author1 = saveAuthor(session, "Стивен", "Кови");
        Author author2 = saveAuthor(session, "Тони", "Роббинс");
        Author author3 = saveAuthor(session, "Наполеон", "Хилл");
        Author author4 = saveAuthor(session, "Роберт", "Кийосаки");
        Author author5 = saveAuthor(session, "Дейл", "Карнеги");

        Book book = saveBook(session, "Java. Библиотеку профессионала. Том 1", (short) 2010, BigDecimal.valueOf(1102.44), List.of(author, author6));
        Book book1 = saveBook(session, "Java. Библиотеку профессионала. Том 2", (short) 2012, BigDecimal.valueOf(954.22), List.of(author, author6));
        Book book2 = saveBook(session, "Java SE 8. Вводный курс", (short) 2015, BigDecimal.valueOf(203.83), List.of(author));
        Book book3 = saveBook(session, "7 навыков высокоэффективных людей", (short) 1989, BigDecimal.valueOf(396.13), List.of(author1));
        Book book4 = saveBook(session, "Разбуди в себе исполина", (short) 1991, BigDecimal.valueOf(576.), List.of(author2));
        Book book5 = saveBook(session, "Думай и богатей", (short) 1937, BigDecimal.valueOf(336.7), List.of(author3));
        Book book6 = saveBook(session, "Богатый папа, бедный папа", (short) 1997, BigDecimal.valueOf(352.88), List.of(author4));
        Book book7 = saveBook(session, "Квадрант денежного потока", (short) 1998, BigDecimal.valueOf(368.99), List.of(author4));
        Book book8 = saveBook(session, "Как перестать беспокоиться и начать жить", (short) 1948, BigDecimal.valueOf(368), List.of(author5));
        Book book9 = saveBook(session, "Как завоевывать друзей и оказывать влияние на людей", (short) 1936, BigDecimal.valueOf(352), List.of(author5));

        User billGates = saveUserAndUserAddress(session, "Bill", "Gates");
        User steveJobs = saveUserAndUserAddress(session, "Steve", "Jobs");
        User sergeyBrin = saveUserAndUserAddress(session, "Sergey", "Brin");

        saveUserDetails(session, billGates, "Bill", "Gates");
        saveUserDetails(session, steveJobs, "Steve", "Jobs");
        saveUserDetails(session, sergeyBrin, "Sergey", "Brin");

        saveUserAddress(session, billGates, "14A");
        saveUserAddress(session, steveJobs, "15");
        saveUserAddress(session, sergeyBrin, "15K4");

        Order order = saveOrder(session, billGates);
        Order order1 = saveOrder(session, billGates);
        Order order2 = saveOrder(session, billGates);
        Order order3 = saveOrder(session, sergeyBrin);

        saveOrderProduct(session, order, book, 25);
        saveOrderProduct(session, order, book1, 25);
        saveOrderProduct(session, order1, book6, 25);
        saveOrderProduct(session, order1, book7, 25);
        saveOrderProduct(session, order1, book5, 25);
        saveOrderProduct(session, order2, book4, 15);
        saveOrderProduct(session, order3, book8, 25);
        saveOrderProduct(session, order3, book9, 25);
        saveOrderProduct(session, order3, book2, 25);
        saveOrderProduct(session, order3, book3, 25);
        saveOrderProduct(session, order3, book4, 10);
    }

    private void saveOrderProduct(Session session, Order order, Book book, int quantity) {
        OrderProduct orderProduct = HibernateTestUtil.createOrderProduct(quantity, book.getPrice());
        order.addOrderProduct(orderProduct);
        book.addOrderProduct(orderProduct);
        session.save(orderProduct);
    }

    private void saveUserAddress(Session session, User user, String house) {
        UserAddress userAddress = HibernateTestUtil.createUserAddress(house);
        user.addUserAddress(userAddress);
        session.save(userAddress);
    }

    private void saveUserDetails(Session session, User user, String firstname, String lastname) {
        UserDetails userDetails = HibernateTestUtil.createUserDetails(firstname, lastname);
        user.addUserDetails(userDetails);
        session.save(userDetails);
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

        return  book;
    }

    private User saveUserAndUserAddress(Session session,
                                        String firstName,
                                        String lastName) {
        User user = HibernateTestUtil.createUser(firstName + lastName + "@Email.com", "pass", Role.ADMIN);
        session.save(user);

        return user;
    }
}
