package com.dmdev.integration;

import com.dmdev.config.ApplicationTestConfiguration;
import com.dmdev.database.dao.repositories.OrderProductRepository;
import com.dmdev.database.entity.Author;
import com.dmdev.database.entity.BaseEntity;
import com.dmdev.database.entity.Book;
import com.dmdev.database.entity.Order;
import com.dmdev.database.entity.OrderProduct;
import com.dmdev.database.entity.User;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderProductIT {

    private static List<BaseEntity> data;
    private static ApplicationContext context;
    private Order order;
    private Book book;
    private OrderProduct orderProduct;
    private Session session;
    private OrderProductRepository repository;

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
    public void prepareOrderProductTable() {
        session = (Session) context.getBean(EntityManager.class);
        repository = context.getBean(OrderProductRepository.class);
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
        session.flush();
    }

    @AfterEach
    void afterTests() {
        session.getTransaction().rollback();
    }

    @Test
    public void createOrderProductTest() {
        OrderProduct orderProduct = HibernateTestUtil.createOrderProduct();
        order.addOrderProduct(orderProduct);
        book.addOrderProduct(orderProduct);

        repository.save(orderProduct);

        assertNotNull(orderProduct.getId());
    }

    @Test
    public void readOrderTest() {
        session.evict(orderProduct);
        Optional<OrderProduct> maybeOrderProduct = repository.findById(orderProduct.getId());

        assertFalse(maybeOrderProduct.isEmpty());
        assertEquals(orderProduct, maybeOrderProduct.get());
    }

    @Test
    public void updateOrderProductTest() {
        orderProduct.setTotalPrice(BigDecimal.valueOf(259.99));
        session.update(orderProduct);
        session.flush();
        session.evict(orderProduct);
        Order actualOrderProduct = session.get(Order.class, order.getId());

        assertEquals(order, actualOrderProduct);
    }

    @Test
    public void deleteOrderProductTest() {
        order.getOrderProducts().remove(orderProduct);
        book.getOrderProducts().remove(orderProduct);
        repository.delete(orderProduct);
        OrderProduct actualOrderProduct = session.get(OrderProduct.class, orderProduct.getId());

        assertNull(actualOrderProduct);
    }

    @Test
    void findAllTest() {
        List<OrderProduct> results = repository.findAll();
        assertThat(results).hasSize(12);

        List<Integer> fullNames = results.stream().map(OrderProduct::getQuantity).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder(5, 10, 15, 25, 25, 25, 25, 25, 25, 25, 25, 25);
    }

    @Test
    void deleteAllByOrders() {
        List<Order> orders = data.stream()
                .filter(Order.class::isInstance)
                .map(Order.class::cast)
                .limit(3)
                .toList();
        List<OrderProduct> expectedOrderProducts = new ArrayList<>(data.stream()
                .filter(OrderProduct.class::isInstance)
                .map(OrderProduct.class::cast)
                .filter(orderProduct1 -> !orders.contains(orderProduct1.getOrder()))
                .toList());
        expectedOrderProducts.add(orderProduct);

        repository.deleteAllByOrders(orders);

        assertThat(expectedOrderProducts).containsAll(repository.findAll());
    }
}
