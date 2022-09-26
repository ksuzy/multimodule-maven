package com.dmdev.crud;

import com.dmdev.entity.Author;
import com.dmdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.Serializable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthorIT {

    private static Author cAuthor;
    private static Author rudAuthor;
    private static SessionFactory sessionFactory;
    private static Session session;
    private static Serializable idOfRud;

    @BeforeEach
    public void prepareAuthorTable(){
        sessionFactory = HibernateUtil.buildSessionFactory();
        session = sessionFactory.openSession();
        cAuthor = HibernateUtil.createAuthorToInsert();
        rudAuthor = HibernateUtil.createAuthorToReadUpdateDelete();
        session.beginTransaction();
        session.createSQLQuery("delete from author").executeUpdate();
        idOfRud = session.save(rudAuthor);
        rudAuthor.setId((Integer) idOfRud);
        session.getTransaction().commit();
    }

    @Test
    public void createAuthorTest(){
            session.beginTransaction();
        Integer id = (Integer) session.save(cAuthor);
            session.getTransaction().commit();
            session.detach(cAuthor);
            cAuthor.setId(id);
            var actualAuthor = session.get(Author.class, id);
            session.detach(actualAuthor);

            assertEquals(cAuthor, actualAuthor);
    }

    @Test
    public void readAuthorTest(){
        session.beginTransaction();
        Author actualAuthor = session.get(Author.class, idOfRud);

        assertEquals(rudAuthor, actualAuthor);
    }

    @Test
    public void updateAuthorTest(){
        session.beginTransaction();
        rudAuthor.setFirstname("IvanAlreadyUpdated");
        session.update(rudAuthor);
        Author actualAuthor = session.get(Author.class, idOfRud);

        assertEquals(rudAuthor, actualAuthor);
    }

    @Test
    public void deleteAuthorTest(){
        session.beginTransaction();
        session.delete(rudAuthor);
        Optional<Author> actualAuthor = Optional.ofNullable(session.get(Author.class, idOfRud));
        assertTrue(actualAuthor.isEmpty());
    }

    @AfterEach
    public void closeSessions(){
        session.close();
        sessionFactory.close();
    }
}