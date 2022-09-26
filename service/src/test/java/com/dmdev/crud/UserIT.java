package com.dmdev.crud;

import com.dmdev.entity.User;
import com.dmdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserIT {

    private static User cUser;
    private static User rudUser;
    private static SessionFactory sessionFactory;
    private static Session session;
    private static Serializable idOfRud;

    @BeforeEach
    public void prepareUserTable(){
        sessionFactory = HibernateUtil.buildSessionFactory();
        session = sessionFactory.openSession();
        cUser = HibernateUtil.createUserToInsert();
        rudUser = HibernateUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.createSQLQuery("delete from user_address").executeUpdate();
        session.createSQLQuery("delete from user_details").executeUpdate();
        session.createSQLQuery("delete from orders").executeUpdate();
        session.createSQLQuery("delete from users").executeUpdate();
        idOfRud = session.save(rudUser);
        rudUser.setId((Integer) idOfRud);
        session.getTransaction().commit();
    }

    @Test
    public void createUserTest(){
        session.beginTransaction();
        Integer id = (Integer) session.save(cUser);
        session.getTransaction().commit();
        session.detach(cUser);
        cUser.setId(id);
        var actualUser = session.get(User.class, id);
        session.detach(actualUser);

        assertEquals(actualUser, cUser);
    }

    @Test
    public void readUserTest(){
        session.beginTransaction();
        User actualUser = session.get(User.class, idOfRud);

        assertEquals(actualUser, rudUser);
    }

    @Test
    public void updateUserTest(){
        session.beginTransaction();
        rudUser.setEmail("emailAlreadyUpdated");
        session.update(rudUser);
        User actualUser = session.get(User.class, idOfRud);

        assertEquals(actualUser, rudUser);
    }

    @Test
    public void deleteAuthorTest(){
        session.beginTransaction();
        session.delete(rudUser);
        Optional<User> actualAuthor = Optional.ofNullable(session.get(User.class, idOfRud));
        assertTrue(actualAuthor.isEmpty());
    }

    @AfterEach
    public void closeSessions(){
        session.close();
        sessionFactory.close();
    }
}