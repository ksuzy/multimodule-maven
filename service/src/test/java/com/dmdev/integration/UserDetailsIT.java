package com.dmdev.integration;

import com.dmdev.config.ApplicationTestConfiguration;
import com.dmdev.database.dao.repositories.UserDetailsRepository;
import com.dmdev.database.entity.BaseEntity;
import com.dmdev.database.entity.User;
import com.dmdev.database.entity.UserDetails;
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
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserDetailsIT {

    private static ApplicationContext context;
    private User user;
    private UserDetails rudUserDetails;
    private Session session;
    private UserDetailsRepository repository;

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
    public void prepareUserDetailsTable() {
        session = (Session) context.getBean(EntityManager.class);
        repository = context.getBean(UserDetailsRepository.class);
        user = HibernateTestUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.save(user);
        rudUserDetails = HibernateTestUtil.createUserDetails();
        user.addUserDetails(rudUserDetails);
        session.save(rudUserDetails);
        session.flush();
    }

    @AfterEach
    void afterTests() {
        session.getTransaction().rollback();
    }

    @Test
    public void createUserDetailsTest() {
        User createUser = HibernateTestUtil.createUserToInsert();
        session.save(createUser);
        UserDetails cUserDetails = HibernateTestUtil.createUserDetails();
        createUser.setUserDetails(cUserDetails);

        repository.save(cUserDetails);

        assertNotNull(cUserDetails.getId());
    }

    @Test
    public void readUserDetailsTest() {
        session.evict(rudUserDetails);
        Optional<UserDetails> maybeUser = repository.findById(rudUserDetails.getId());

        assertFalse(maybeUser.isEmpty());
        assertEquals(rudUserDetails, maybeUser.get());
    }

    @Test
    public void updateUserDetailsTest() {
        rudUserDetails.setPhone("+79111501823");
        repository.update(rudUserDetails);
        session.flush();
        session.evict(rudUserDetails);
        UserDetails actualUserDetails = session.get(UserDetails.class, rudUserDetails.getId());

        assertEquals(rudUserDetails, actualUserDetails);
    }

    @Test
    public void deleteUserDetailsTest() {
        user.setUserDetails(null);
        repository.delete(rudUserDetails);
        UserDetails actualUserDetails = session.get(UserDetails.class, rudUserDetails.getId());

        assertNull(actualUserDetails);
    }

    @Test
    void findAllTest() {
        List<UserDetails> results = repository.findAll();
        assertThat(results).hasSize(4);

        List<String> fullNames = results.stream().map(userDetails -> userDetails.getUser().fullName()).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates", "Steve Jobs", "Sergey Brin", "Pavel Pavlov");
    }
}