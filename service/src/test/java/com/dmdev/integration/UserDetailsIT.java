package com.dmdev.integration;

import com.dmdev.dao.repositories.UserDetailsRepository;
import com.dmdev.entity.BaseEntity;
import com.dmdev.entity.User;
import com.dmdev.entity.UserDetails;
import com.dmdev.util.HibernateTestUtil;
import com.dmdev.util.TestDataImporter;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.dmdev.util.HibernateTestUtil.sessionFactory;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserDetailsIT {

    private User user;
    private UserDetails rudUserDetails;
    private Session session;
    private UserDetailsRepository repository;
    private static List<BaseEntity> data;

    @BeforeAll
    public static void initialization() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
        data = TestDataImporter.importData(sessionFactory);
    }

    @AfterAll
    public static void finish() {
        sessionFactory.close();
    }

    @BeforeEach
    public void prepareUserDetailsTable() {
        session = sessionFactory.getCurrentSession();
        repository = new UserDetailsRepository(session);
        user = HibernateTestUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.save(user);
        rudUserDetails = HibernateTestUtil.createUserDetails();
        user.addUserDetails(rudUserDetails);
        session.save(rudUserDetails);
        session.flush();
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

    @AfterEach
    void closeSessions() {
        session.getTransaction().rollback();
    }
}