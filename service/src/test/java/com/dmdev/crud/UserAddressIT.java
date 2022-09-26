package com.dmdev.crud;

import com.dmdev.entity.User;
import com.dmdev.entity.UserAddress;
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

class UserAddressIT {

    private static UserAddress cUserAddress;
    private static UserAddress rudUserAddress;
    private static SessionFactory sessionFactory;
    private static Session session;
    private static Serializable userIdOfRud;

    @BeforeEach
    public void prepareUserAddressTable(){
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
        UserDetails rudUserDetails = HibernateUtil.createUserDetails(userId);
        UserDetails cUserDetails = HibernateUtil.createUserDetails(createUserId);
        userIdOfRud = session.save(rudUserDetails);
        userIdOfRud = session.save(cUserDetails);
        rudUserAddress = HibernateUtil.createUserAddress(userId);
        cUserAddress = HibernateUtil.createUserAddress(createUserId);
        session.getTransaction().commit();
        session.beginTransaction();
        userIdOfRud = session.save(rudUserAddress);
        session.getTransaction().commit();
    }

    @Test
    public void createUserAddressTest(){
        session.beginTransaction();
        Integer userId = (Integer) session.save(cUserAddress);
        session.getTransaction().commit();
        session.detach(cUserAddress);
        var actualUserAddress = session.get(UserAddress.class, userId);
        session.detach(actualUserAddress);

        assertEquals(actualUserAddress, cUserAddress);
    }

    @Test
    public void readUserAddressTest(){
        session.beginTransaction();
        UserAddress actualUserAddress = session.get(UserAddress.class, userIdOfRud);

        assertEquals(actualUserAddress, rudUserAddress);
    }

    @Test
    public void updateUserAddressTest(){
        session.beginTransaction();
        rudUserAddress.setDistrict("Дистрикт №9");
        session.update(rudUserAddress);
        UserAddress actualUserAddress = session.get(UserAddress.class, userIdOfRud);

        assertEquals(actualUserAddress, rudUserAddress);
    }

    @Test
    public void deleteUserAddressTest(){
        session.beginTransaction();
        session.delete(rudUserAddress);
        Optional<UserAddress> actualUserAddress = Optional.ofNullable(session.get(UserAddress.class, userIdOfRud));
        assertTrue(actualUserAddress.isEmpty());
    }

    @AfterEach
    public void closeSessions(){
        session.close();
        sessionFactory.close();
    }
}