package com.dmdev.integration;

import com.dmdev.config.ApplicationTestConfiguration;
import com.dmdev.database.dao.repositories.UserRepository;
import com.dmdev.database.pool.ConnectionPool;
import com.dmdev.database.entity.User;
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
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserIT {

    private static ApplicationContext context;
    private User rudUser;
    private Session session;
    private UserRepository repository;

    @BeforeAll
    public static void initialization() {
        context = new AnnotationConfigApplicationContext(ApplicationTestConfiguration.class);
        TestDataImporter.importData(context.getBean(ConnectionPool.class).sessionFactory());
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
    public void prepareUserTable() {
        session = (Session) context.getBean(EntityManager.class);
        repository = context.getBean(UserRepository.class);
        rudUser = HibernateTestUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.save(rudUser);
        session.flush();
        rudUser.addUserDetails(HibernateTestUtil.createUserDetails("Ivan", "Ivanov"));
    }

    @AfterEach
    void afterTests() {
        session.getTransaction().rollback();
    }

    @Test
    public void createUserTest() {
        User cUser = HibernateTestUtil.createUserToInsert();

        repository.save(cUser);

        assertNotNull(cUser.getId());
    }

    @Test
    public void readUserTest() {
        session.evict(rudUser);
        Optional<User> maybeUser = repository.findById(rudUser.getId());

        assertFalse(maybeUser.isEmpty());
        assertEquals(rudUser, maybeUser.get());
    }

    @Test
    public void updateUserTest() {
        rudUser.setEmail("emailAlreadyUpdated");
        repository.update(rudUser);
        session.flush();
        session.evict(rudUser);

        User maybeUser = session.get(User.class, rudUser.getId());
        assertEquals(rudUser, maybeUser);
    }

    @Test
    public void deleteUserTest() {
        repository.delete(rudUser);

        User maybeUser = session.get(User.class, rudUser.getId());
        assertNull(maybeUser);
    }

    @Test
    void findAllTest() {
        List<User> results = repository.findAll();
        assertThat(results).hasSize(4);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates", "Steve Jobs", "Sergey Brin", "Ivan Ivanov");
    }

    @Test
    void findAllWithGraphsTest() {
        List<User> results = repository.findAllWithGraphs();
        assertThat(results).hasSize(4);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates", "Steve Jobs", "Sergey Brin", "Ivan Ivanov");
    }

    @Test
    void findAllByFirstnameTest() {
        List<User> results = repository.findAllByFirstname("Bill");
        assertThat(results).hasSize(1);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates");
    }
}