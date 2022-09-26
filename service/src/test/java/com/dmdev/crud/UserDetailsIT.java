package com.dmdev.crud;

import com.dmdev.entity.User;
import com.dmdev.entity.UserDetails;
import com.dmdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.Serializable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsIT {

    private static UserDetails cUserDetails;
    private static UserDetails rudUserDetails;
    private static SessionFactory sessionFactory;
    private static Session session;
    private static Serializable userIdOfRud;

    @BeforeEach
    public void prepareUserDetailsTable(){
        sessionFactory = HibernateUtil.buildSessionFactory();
        session = sessionFactory.openSession();
        User user = HibernateUtil.createUserToReadUpdateDelete();
        User createUser = HibernateUtil.createUserToInsert();
        session.beginTransaction();
        session.createSQLQuery("delete from user_address").executeUpdate();
        session.createSQLQuery("delete from user_details").executeUpdate();
        session.createSQLQuery("delete from orders").executeUpdate();
        session.createSQLQuery("delete from users").executeUpdate();
        Integer userId = (Integer) session.save(user);
        Integer createUserId = (Integer) session.save(createUser);
        session.getTransaction().commit();
        rudUserDetails = HibernateUtil.createUserDetails(userId);
        cUserDetails = HibernateUtil.createUserDetails(createUserId);
        session.beginTransaction();
        userIdOfRud = session.save(rudUserDetails);
        session.getTransaction().commit();
    }

    @Test
    public void createUserDetailsTest(){
        session.beginTransaction();
        Integer userId = (Integer) session.save(cUserDetails);
        session.getTransaction().commit();
        session.detach(cUserDetails);
        var actualUserDetails = session.get(UserDetails.class, userId);
        session.detach(actualUserDetails);

        assertEquals(actualUserDetails, cUserDetails);
    }

    @Test
    public void readUserDetailsTest(){
        session.beginTransaction();
        UserDetails actualUserDetails = session.get(UserDetails.class, userIdOfRud);

        assertEquals(actualUserDetails, rudUserDetails);
    }

    @Test
    public void updateUserDetailsTest(){
        session.beginTransaction();
        rudUserDetails.setPhone("+79111501823");
        session.update(rudUserDetails);
        UserDetails actualUserDetails = session.get(UserDetails.class, userIdOfRud);

        assertEquals(actualUserDetails, rudUserDetails);
    }

    @Test
    public void deleteUserDetailsTest(){
        session.beginTransaction();
        session.delete(rudUserDetails);
        Optional<UserDetails> actualUserDetails = Optional.ofNullable(session.get(UserDetails.class, userIdOfRud));
        assertTrue(actualUserDetails.isEmpty());
    }

    @AfterEach
    public void closeSessions(){
        session.close();
        sessionFactory.close();
    }
}