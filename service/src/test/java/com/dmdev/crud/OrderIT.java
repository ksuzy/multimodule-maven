package com.dmdev.crud;

import com.dmdev.entity.Order;
import com.dmdev.entity.User;
import com.dmdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderIT {

    private static Order cOrder;
    private static Order rudOrder;
    private static SessionFactory sessionFactory;
    private static Session session;
    private static Serializable idOfRud;

    @BeforeEach
    public void prepareOrderTable(){
        sessionFactory = HibernateUtil.buildSessionFactory();
        session = sessionFactory.openSession();
        User user = HibernateUtil.createUserToInsert();
        session.beginTransaction();
        session.createSQLQuery("delete from user_address").executeUpdate();
        session.createSQLQuery("delete from user_details").executeUpdate();
        session.createSQLQuery("delete from orders").executeUpdate();
        session.createSQLQuery("delete from users").executeUpdate();
        Integer clientId = (Integer) session.save(user);
        session.getTransaction().commit();
        cOrder = HibernateUtil.createOrder(clientId);
        rudOrder = HibernateUtil.createOrder(clientId);
        session.beginTransaction();
        idOfRud = session.save(rudOrder);
        rudOrder.setId((Long) idOfRud);
        session.getTransaction().commit();
    }

    @Test
    public void createOrderTest(){
        session.beginTransaction();
        Long id = (Long) session.save(cOrder);
        session.getTransaction().commit();
        session.detach(cOrder);
        cOrder.setId(id);
        var actualOrder = session.get(Order.class, id);
        session.detach(actualOrder);

        assertEquals(actualOrder, cOrder);
    }

    @Test
    public void readOrderTest(){
        session.beginTransaction();
        Order actualOrder = session.get(Order.class, idOfRud);

        assertEquals(actualOrder, rudOrder);
    }

    @Test
    public void updateOrderTest(){
        session.beginTransaction();
        rudOrder.setPrice(BigDecimal.valueOf(200.14));
        session.update(rudOrder);
        Order actualOrder = session.get(Order.class, idOfRud);

        assertEquals(actualOrder, rudOrder);
    }

    @Test
    public void deleteAuthorTest(){
        session.beginTransaction();
        session.delete(rudOrder);
        Optional<Order> actualOrder = Optional.ofNullable(session.get(Order.class, idOfRud));
        assertTrue(actualOrder.isEmpty());
    }

    @AfterEach
    public void closeSessions(){
        session.close();
        sessionFactory.close();
    }
}