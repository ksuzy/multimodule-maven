package com.dmdev.crud;

import com.dmdev.entity.Order;
import com.dmdev.entity.User;
import com.dmdev.entity.fields.Status;
import com.dmdev.util.HibernateTestUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.dmdev.util.HibernateTestUtil.sessionFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderIT {

    private User user;
    private Order rudOrder;
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
    public void prepareOrderTable(){
        session = sessionFactory.openSession();
        user = HibernateTestUtil.createUserToReadUpdateDelete();
        rudOrder = HibernateTestUtil.createOrder();
        user.addOrder(rudOrder);
        session.beginTransaction();
        session.save(user);
        session.save(rudOrder);
        session.getTransaction().commit();
    }

    @Test
    public void createOrderTest(){
        Order order = HibernateTestUtil.createOrder();
        user.addOrder(order);

        session.beginTransaction();
        session.save(order);
        session.getTransaction().commit();

        assertNotNull(order.getId());
    }

    @Test
    public void readOrderTest(){
        session.beginTransaction();
        session.evict(rudOrder);
        session.flush();
        Order actualOrder = session.get(Order.class, rudOrder.getId());
        session.getTransaction().commit();

        assertEquals(rudOrder, actualOrder);
    }

    @Test
    public void updateOrderTest(){
        session.beginTransaction();
        rudOrder.setStatus(Status.CLOSED);
        session.update(rudOrder);
        session.evict(rudOrder);
        session.flush();
        Order actualOrder = session.get(Order.class, rudOrder.getId());

        assertEquals(rudOrder, actualOrder);
    }

    @Test
    public void deleteAuthorTest(){
        session.beginTransaction();
        user.getOrders().remove(rudOrder);
        session.flush();
        Order actualOrder = session.get(Order.class, rudOrder.getId());
        session.getTransaction().commit();

        assertNull(actualOrder);
    }

    @AfterEach
    public void closeSessions(){
        session.close();
    }
}