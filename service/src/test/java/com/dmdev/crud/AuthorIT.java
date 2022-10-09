package com.dmdev.crud;

import com.dmdev.entity.Author;
import com.dmdev.util.HibernateTestUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.dmdev.util.HibernateTestUtil.sessionFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthorIT {

    private Author rudAuthor;
    private Session session;

    @BeforeAll
    public static void initialization(){
        sessionFactory = HibernateTestUtil.buildSessionFactory();
    }

    @BeforeEach
    public void prepareAuthorTable(){
        session = sessionFactory.openSession();
        rudAuthor = HibernateTestUtil.createAuthorToReadUpdateDelete();
        session.beginTransaction();
        session.save(rudAuthor);
        session.getTransaction().commit();
    }

    @Test
    public void createAuthorTest(){
        Author cAuthor = HibernateTestUtil.createAuthorToInsert();

        session.beginTransaction();
        session.save(cAuthor);
        session.getTransaction().commit();

            assertNotNull(cAuthor.getId());
    }

    @Test
    public void readAuthorTest(){
        session.beginTransaction();
        session.evict(rudAuthor);
        Author actualAuthor = session.get(Author.class, rudAuthor.getId());
        session.getTransaction().commit();

        assertEquals(rudAuthor, actualAuthor);
    }

    @Test
    public void updateAuthorTest(){
        session.beginTransaction();
        rudAuthor.setFirstname("IvanAlreadyUpdated");
        session.update(rudAuthor);
        session.flush();
        session.evict(rudAuthor);
        Author actualAuthor = session.get(Author.class, rudAuthor.getId());
        session.getTransaction().commit();

        assertEquals(rudAuthor, actualAuthor);
    }

    @Test
    public void deleteAuthorTest(){
        session.beginTransaction();
        session.delete(rudAuthor);
        Author actualAuthor = session.get(Author.class, rudAuthor.getId());
        session.getTransaction().commit();

        assertNull(actualAuthor);
    }

    @AfterEach
    public void closeSessions(){
        session.close();
    }
}