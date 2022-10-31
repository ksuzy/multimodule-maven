package com.dmdev.integration;

import com.dmdev.config.ApplicationTestConfiguration;
import com.dmdev.database.dao.repositories.OrderRepository;
import com.dmdev.database.entity.BaseEntity;
import com.dmdev.database.entity.Order;
import com.dmdev.database.entity.User;
import com.dmdev.database.entity.fields.Status;
import com.dmdev.database.pool.ConnectionPool;
import com.dmdev.exceptions.SpringContextCloseException;
import com.dmdev.util.HibernateTestUtil;
import com.dmdev.util.TestDataImporter;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;
import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderIT {

    private static List<BaseEntity> data;
    private static ApplicationContext context;
    private User user;
    private Order rudOrder;
    private Session session;
    private OrderRepository repository;

    @BeforeAll
    public static void initialization() {
        context = new AnnotationConfigApplicationContext(ApplicationTestConfiguration.class);
        data = TestDataImporter.importData(context.getBean(ConnectionPool.class).sessionFactory());
    }

    @AfterAll
    public static void finish() {
        try {
            ((Closeable) context).close();
        } catch (IOException e) {
            throw new SpringContextCloseException(e);
        }
    }

    @BeforeEach
    public void prepareOrderTable() {
        session = (Session) context.getBean(EntityManager.class);
        repository = context.getBean(OrderRepository.class);
        user = HibernateTestUtil.createUserToReadUpdateDelete();
        rudOrder = HibernateTestUtil.createOrder();
        user.addOrder(rudOrder);
        session.beginTransaction();
        session.save(user);
        session.save(rudOrder);
        session.flush();
    }

    @AfterEach
    void afterTests() {
        session.getTransaction().rollback();
    }

    @Test
    public void createOrderTest() {
        Order order = HibernateTestUtil.createOrder();
        user.addOrder(order);

        repository.save(order);

        assertNotNull(order.getId());
    }

    @Test
    public void readOrderTest() {
        session.evict(rudOrder);
        Optional<Order> maybeOrder = repository.findById(rudOrder.getId());

        assertFalse(maybeOrder.isEmpty());
        assertEquals(rudOrder, maybeOrder.get());
    }

    @Test
    public void updateOrderTest() {
        rudOrder.setStatus(Status.CLOSED);
        repository.update(rudOrder);
        session.evict(rudOrder);
        Order actualOrder = session.get(Order.class, rudOrder.getId());

        assertEquals(rudOrder, actualOrder);
    }

    @Test
    public void deleteOrderTest() {
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
        List<Order> results = repository.findAllByUserId(userForFind.get().getId());
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
    void deleteOrdersMadeEarlierDateTest() {
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
}