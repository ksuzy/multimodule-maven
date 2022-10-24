package com.dmdev.integration;

import com.dmdev.dao.repositories.OrderRepository;
import com.dmdev.entity.BaseEntity;
import com.dmdev.entity.Order;
import com.dmdev.entity.User;
import com.dmdev.entity.fields.Status;
import com.dmdev.util.HibernateTestUtil;
import com.dmdev.util.TestDataImporter;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static com.dmdev.util.HibernateTestUtil.sessionFactory;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderIT {

    private User user;
    private Order rudOrder;
    private Session session;
    private OrderRepository repository;
    private static List<BaseEntity> data;

    @BeforeAll
    public static void initialization(){
        sessionFactory = HibernateTestUtil.buildSessionFactory();
        data = TestDataImporter.importData(sessionFactory);
    }

    @AfterAll
    public static void finish(){
        sessionFactory.close();
    }

    @BeforeEach
    public void prepareOrderTable(){
        session = sessionFactory.getCurrentSession();
        repository = new OrderRepository(session);
        user = HibernateTestUtil.createUserToReadUpdateDelete();
        rudOrder = HibernateTestUtil.createOrder();
        user.addOrder(rudOrder);
        session.beginTransaction();
        session.save(user);
        session.save(rudOrder);
        session.flush();
    }

    @Test
    public void createOrderTest(){
        Order order = HibernateTestUtil.createOrder();
        user.addOrder(order);

        repository.save(order);

        assertNotNull(order.getId());
    }

    @Test
    public void readOrderTest(){
        session.evict(rudOrder);
        Optional<Order> maybeOrder = repository.findById(rudOrder.getId());

        assertFalse(maybeOrder.isEmpty());
        assertEquals(rudOrder, maybeOrder.get());
    }

    @Test
    public void updateOrderTest(){
        rudOrder.setStatus(Status.CLOSED);
        repository.update(rudOrder);
        session.evict(rudOrder);
        Order actualOrder = session.get(Order.class, rudOrder.getId());

        assertEquals(rudOrder, actualOrder);
    }

    @Test
    public void deleteOrderTest(){
        user.getOrders().remove(rudOrder);
        repository.delete(rudOrder);
        Order actualOrder = session.get(Order.class, rudOrder.getId());

        assertNull(actualOrder);
    }

    @Test
    void findAllTest() {
        List<Order> results = repository.findAll();
        assertThat(results).hasSize(5);

        List<Status> fullNames = results.stream().map(Order::getStatus).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder(Status.OPEN, Status.OPEN, Status.OPEN, Status.OPEN, Status.OPEN);
    }

    @Test
    void findAllByUserTest() {
        Optional<User> userForFind = data.stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .findFirst();

        assertFalse(userForFind.isEmpty());
        List<Order> results = repository.findAllByUser(userForFind.get());
        assertThat(results).hasSize(userForFind.get().getOrders().size());
    }

    @Test
    void updateStatusByIdTest() {
        repository.updateStatusById(rudOrder.getId(), Status.CLOSED);
        session.flush();
        session.evict(rudOrder);
        Order actualOrder = session.get(Order.class, rudOrder.getId());

        assertEquals(rudOrder, actualOrder);
    }

    @Test
    void deleteOrdersMadeEarlierDateTest(){
        List<BaseEntity> expectedOrders = data.stream()
                .filter(Order.class::isInstance)
                .toList();

        repository.deleteOrdersMadeEarlierDate(LocalDateTime.now().minus(Period.ofDays(2)));

        List<Order> actualOrders = repository.findAll();
        assertThat(actualOrders).hasSize(expectedOrders.size());

        repository.deleteOrdersMadeEarlierDate(LocalDateTime.now());

        actualOrders = repository.findAll();
        assertThat(actualOrders).hasSize(0);
    }

    @AfterEach
    void closeSessions() {
        session.getTransaction().rollback();
    }
}