package com.dmdev.integration;

import com.dmdev.config.ApplicationTestConfiguration;
import com.dmdev.database.dao.repositories.UserAddressRepository;
import com.dmdev.database.entity.BaseEntity;
import com.dmdev.database.entity.User;
import com.dmdev.database.entity.UserAddress;
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
import static org.junit.jupiter.api.Assertions.assertNull;

class UserAddressIT {

    private static ApplicationContext context;
    private User user;
    private UserAddress rudUserAddress;
    private Session session;
    private UserAddressRepository repository;

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
    public void prepareUserAddressTable() {
        session = (Session) context.getBean(EntityManager.class);
        repository = context.getBean(UserAddressRepository.class);
        user = HibernateTestUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.save(user);
        rudUserAddress = HibernateTestUtil.createUserAddress();
        user.addUserAddress(rudUserAddress);
        session.save(rudUserAddress);
        session.flush();
    }


    @AfterEach
    void afterTests() {
        session.getTransaction().rollback();
    }

    @Test
    public void createUserAddressTest() {
        User createUser = HibernateTestUtil.createUserToInsert();

        session.save(createUser);

        UserAddress cUserAddress = HibernateTestUtil.createUserAddress();
        createUser.addUserAddress(cUserAddress);

        repository.save(cUserAddress);
        session.flush();
        session.evict(cUserAddress);
        UserAddress actualUserAddress = session.get(UserAddress.class, cUserAddress.getId());

        assertEquals(cUserAddress, actualUserAddress);
    }

    @Test
    public void readUserAddressTest() {
        session.evict(rudUserAddress);
        Optional<UserAddress> actualUserAddress = repository.findById(rudUserAddress.getId());

        assertFalse(actualUserAddress.isEmpty());
        assertEquals(rudUserAddress, actualUserAddress.get());
    }

    @Test
    public void updateUserAddressTest() {
        rudUserAddress.setRegion("Guadeloupe");
        repository.update(rudUserAddress);
        session.flush();
        session.evict(rudUserAddress);
        UserAddress actualUserAddress = session.get(UserAddress.class, rudUserAddress.getId());

        assertEquals(rudUserAddress, actualUserAddress);
    }

    @Test
    public void deleteUserAddressTest() {
        user.setUserAddress(null);
        repository.delete(rudUserAddress);
        UserAddress actualUserAddress = session.get(UserAddress.class, rudUserAddress.getId());

        assertNull(actualUserAddress);
    }

    @Test
    void findAllTest() {
        List<UserAddress> results = repository.findAll();
        assertThat(results).hasSize(4);

        List<String> fullNames = results.stream().map(UserAddress::getHouse).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("14A", "15", "15K4", "14");
    }
}