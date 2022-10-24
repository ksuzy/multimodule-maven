package com.dmdev.integration;

import com.dmdev.dao.repositories.UserRepository;
import com.dmdev.dto.UserFilter;
import com.dmdev.entity.BaseEntity;
import com.dmdev.entity.User;
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

class UserIT {

    private User rudUser;
    private Session session;
    private UserRepository repository;
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
    public void prepareUserTable(){
        session = sessionFactory.getCurrentSession();
        repository = new UserRepository(session);
        rudUser = HibernateTestUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.save(rudUser);
        session.flush();
        rudUser.addUserDetails(HibernateTestUtil.createUserDetails("Ivan", "Ivanov"));
    }

    @Test
    public void createUserTest(){
        User cUser = HibernateTestUtil.createUserToInsert();

        repository.save(cUser);

        assertNotNull(cUser.getId());
    }

    @Test
    public void readUserTest(){
        session.evict(rudUser);
        Optional<User> maybeUser = repository.findById(rudUser.getId());

        assertFalse(maybeUser.isEmpty());
        assertEquals(rudUser, maybeUser.get());
    }

    @Test
    public void updateUserTest(){
        rudUser.setEmail("emailAlreadyUpdated");
        repository.update(rudUser);
        session.flush();
        session.evict(rudUser);

        User maybeUser = session.get(User.class, rudUser.getId());
        assertEquals(rudUser, maybeUser);
    }

    @Test
    public void deleteUserTest(){
        repository.delete(rudUser);

        User maybeUser = session.get(User.class, rudUser.getId());
        assertNull(maybeUser);
    }

    @Test
    void findAllTest(){
        List<User> results = repository.findAll();
        assertThat(results).hasSize(4);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates", "Steve Jobs", "Sergey Brin", "Ivan Ivanov");
    }

    @Test
    void findAllWithGraphsTest(){
        List<User> results = repository.findAllWithGraphs();
        assertThat(results).hasSize(4);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates", "Steve Jobs", "Sergey Brin", "Ivan Ivanov");
    }

    @Test
    void findAllByFirstnameTest() {
        UserFilter filter = UserFilter.builder()
                .firstname("Bill")
                .build();

        List<User> results = repository.findAllByFirstname(filter);
        assertThat(results).hasSize(1);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates");
    }

    @AfterEach
    void rollbackTransaction(){
        session.getTransaction().rollback();
    }
}