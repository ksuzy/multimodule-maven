package com.dmdev.crud;

import com.dmdev.entity.Author;
import com.dmdev.entity.Book;
import com.dmdev.entity.Order;
import com.dmdev.entity.OrderProduct;
import com.dmdev.entity.User;
import com.dmdev.util.HibernateTestUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.dmdev.util.HibernateTestUtil.sessionFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderProductIT {
    private Order order;
    private Book book;
    private OrderProduct orderProduct;
    private Session session;

    @BeforeAll
    public static void initialization(){
        sessionFactory = HibernateTestUtil.buildSessionFactory();
    }

    @AfterAll
    public static void finish(){
        sessionFactory.close();
    }

    @BeforeEach
    public void prepareOrderProductTable() {
        session = sessionFactory.openSession();
        User user = HibernateTestUtil.createUserToReadUpdateDelete();
        order = HibernateTestUtil.createOrder();
        user.addOrder(order);
        Author author = HibernateTestUtil.createAuthorToReadUpdateDelete();
        book = HibernateTestUtil.createBook(author);
        orderProduct = HibernateTestUtil.createOrderProduct();
        order.addOrderProduct(orderProduct);
        book.addOrderProduct(orderProduct);
        session.beginTransaction();
        session.save(user);
        session.save(order);
        session.save(author);
        session.save(book);
        session.save(orderProduct);
        session.getTransaction().commit();
    }

    @Test
    public void createOrderProductTest() {
        OrderProduct orderProduct = HibernateTestUtil.createOrderProduct();
        order.addOrderProduct(orderProduct);
        book.addOrderProduct(orderProduct);

        session.beginTransaction();
        session.save(orderProduct);
        session.getTransaction().commit();

        assertNotNull(orderProduct.getId());
    }

    @Test
    public void readOrderTest() {
        session.beginTransaction();
        session.evict(orderProduct);
        session.flush();
        OrderProduct actualOrderProduct = session.get(OrderProduct.class, orderProduct.getId());
        session.getTransaction().commit();

        assertEquals(orderProduct, actualOrderProduct);
    }

    @Test
    public void updateOrderProductTest() {
        session.beginTransaction();
        orderProduct.setTotalPrice(BigDecimal.valueOf(259.99));
        session.update(orderProduct);
        session.evict(orderProduct);
        session.flush();
        Order actualOrderProduct = session.get(Order.class, order.getId());
        session.getTransaction().commit();

        assertEquals(order, actualOrderProduct);
    }

    @Test
    public void deleteOrderProductTest() {
        session.beginTransaction();
        order.getOrderProducts().remove(orderProduct);
        book.getOrderProducts().remove(orderProduct);
        session.delete(orderProduct);
        OrderProduct actualOrderProduct = session.get(OrderProduct.class, orderProduct.getId());
        session.getTransaction().commit();

        assertNull(actualOrderProduct);
    }

    @AfterEach
    public void closeSessions() {
        session.close();
    }
}
