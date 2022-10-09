package com.dmdev.crud;

import com.dmdev.entity.User;
import com.dmdev.entity.UserAddress;
import com.dmdev.util.HibernateTestUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.dmdev.util.HibernateTestUtil.sessionFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserAddressIT {

    private User user;
    private UserAddress rudUserAddress;
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
    public void prepareUserAddressTable(){
        session = sessionFactory.openSession();
        user = HibernateTestUtil.createUserToReadUpdateDelete();
        session.beginTransaction();
        session.save(user);
        rudUserAddress = HibernateTestUtil.createUserAddress();
        user.addUserAddress(rudUserAddress);
        session.save(rudUserAddress);
        session.getTransaction().commit();
    }

    @Test
    public void createUserAddressTest(){
        User createUser = HibernateTestUtil.createUserToInsert();

        session.beginTransaction();
        session.save(createUser);
        session.getTransaction().commit();

        UserAddress cUserAddress = HibernateTestUtil.createUserAddress();
        createUser.addUserAddress(cUserAddress);

        session.beginTransaction();
        session.save(cUserAddress);
        session.flush();
        session.evict(cUserAddress);
        session.flush();
        UserAddress actualUserAddress = session.get(UserAddress.class, cUserAddress.getId());

        assertEquals(cUserAddress, actualUserAddress);
    }

    @Test
    public void readUserAddressTest(){
        session.beginTransaction();
        user.setUserAddress(null);
        session.evict(rudUserAddress);
        UserAddress actualUserAddress = session.get(UserAddress.class, rudUserAddress.getId());

        assertEquals(rudUserAddress, actualUserAddress);
    }

    @Test
    public void updateUserAddressTest(){
        session.beginTransaction();
        rudUserAddress.setRegion("Guadeloupe");
        session.update(rudUserAddress);
        session.evict(rudUserAddress);
        session.flush();
        UserAddress actualUserAddress = session.get(UserAddress.class, rudUserAddress.getId());

        assertEquals(rudUserAddress, actualUserAddress);
    }

    @Test
    public void deleteUserAddressTest(){
        session.beginTransaction();
        user.setUserAddress(null);
        session.delete(rudUserAddress);
        UserAddress actualUserAddress = session.get(UserAddress.class, rudUserAddress.getId());
        session.getTransaction().commit();

        assertNull(actualUserAddress);
    }

    @AfterEach
    public void closeSessions(){
        session.close();
    }
}