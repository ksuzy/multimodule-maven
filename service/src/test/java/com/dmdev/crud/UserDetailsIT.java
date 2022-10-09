package com.dmdev.crud;

import com.dmdev.entity.User;
import com.dmdev.entity.UserDetails;
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

class UserDetailsIT {

    private User user;
    private UserDetails rudUserDetails;
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
    public void prepareUserDetailsTable(){
        session = sessionFactory.openSession();
        user = HibernateTestUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.save(user);
        rudUserDetails = HibernateTestUtil.createUserDetails();
        user.addUserDetails(rudUserDetails);
        session.getTransaction().commit();
    }

    @Test
    public void createUserDetailsTest(){
        User createUser = HibernateTestUtil.createUserToInsert();
        session.beginTransaction();
        session.save(createUser);
        session.getTransaction().commit();
        UserDetails cUserDetails = HibernateTestUtil.createUserDetails();
        createUser.setUserDetails(cUserDetails);

        session.beginTransaction();
        session.save(cUserDetails);
        session.getTransaction().commit();

        assertNotNull(cUserDetails.getId());
    }

    @Test
    public void readUserDetailsTest(){
        session.beginTransaction();
        user.setUserDetails(null);
        session.evict(rudUserDetails);
        UserDetails actualUserDetails = session.get(UserDetails.class, rudUserDetails.getId());

        assertEquals(rudUserDetails, actualUserDetails);
    }

    @Test
    public void updateUserDetailsTest(){
        session.beginTransaction();
        rudUserDetails.setPhone("+79111501823");
        session.update(rudUserDetails);
        session.evict(rudUserDetails);
        session.flush();
        UserDetails actualUserDetails = session.get(UserDetails.class, rudUserDetails.getId());

        assertEquals(rudUserDetails, actualUserDetails);
    }

    @Test
    public void deleteUserDetailsTest(){
        session.beginTransaction();
        user.setUserDetails(null);
        session.delete(rudUserDetails);
        UserDetails actualUserDetails = session.get(UserDetails.class, rudUserDetails.getId());

        assertNull(actualUserDetails);
    }

    @AfterEach
    public void closeSessions(){
        session.close();
    }
}