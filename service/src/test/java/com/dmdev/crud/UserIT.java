package com.dmdev.crud;

import com.dmdev.entity.User;
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

class UserIT {

    private User rudUser;
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
    public void prepareUserTable(){
        session = sessionFactory.openSession();
        rudUser = HibernateTestUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.save(rudUser);
        session.getTransaction().commit();
    }

    @Test
    public void createUserTest(){
        User cUser = HibernateTestUtil.createUserToInsert();

        session.beginTransaction();
        session.save(cUser);
        session.getTransaction().commit();

        assertNotNull(cUser.getId());
    }

    @Test
    public void readUserTest(){
        session.beginTransaction();
        session.evict(rudUser);
        User actualUser = session.get(User.class, rudUser.getId());
        session.getTransaction().commit();

        assertEquals(rudUser, actualUser);
    }

    @Test
    public void updateUserTest(){
        session.beginTransaction();
        rudUser.setEmail("emailAlreadyUpdated");
        session.update(rudUser);
        session.flush();
        session.evict(rudUser);
        User actualUser = session.get(User.class, rudUser.getId());

        assertEquals(rudUser, actualUser);
    }

    @Test
    public void deleteAuthorTest(){
        session.beginTransaction();
        session.delete(rudUser);
        User actualAuthor = session.get(User.class, rudUser.getId());
        session.getTransaction().commit();

        assertNull(actualAuthor);
    }

    @AfterEach
    public void closeSessions(){
        session.close();
    }
}