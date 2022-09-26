package com.dmdev.crud;

import com.dmdev.entity.Book;
import com.dmdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookIT {

    private static Book cBook;
    private static Book rudBook;

    private static SessionFactory sessionFactory;
    private static Session session;
    private static Serializable idOfRud;

    @BeforeEach
    public void prepareBookTable(){
        sessionFactory = HibernateUtil.buildSessionFactory();
        session = sessionFactory.openSession();
        cBook = HibernateUtil.createBookToInsert();
        rudBook = HibernateUtil.createBookToReadUpdateDelete();
        session.beginTransaction();
        session.createSQLQuery("delete from orders").executeUpdate();
        session.createSQLQuery("delete from book").executeUpdate();
        idOfRud = session.save(rudBook);
        rudBook.setId((Long) idOfRud);
        session.getTransaction().commit();
    }

    @Test
    public void createBookTest(){
        session.beginTransaction();
        Long id = (Long) session.save(cBook);
        session.getTransaction().commit();
        session.detach(cBook);
        cBook.setId(id);
        var actualBook = session.get(Book.class, id);
        session.detach(actualBook);

        assertEquals(cBook, actualBook);
    }

    @Test
    public void readBookTest(){
        session.beginTransaction();
        Book actualBook = session.get(Book.class, idOfRud);

        assertEquals(rudBook, actualBook);
    }

    @Test
    public void updateBookTest(){
        session.beginTransaction();
        rudBook.setName("bookAlreadyUpdated");
        session.update(rudBook);
        Book actualBook = session.get(Book.class, idOfRud);

        assertEquals(rudBook, actualBook);
    }

    @Test
    public void deleteAuthorTest(){
        session.beginTransaction();
        session.delete(rudBook);
        Optional<Book> actualBook = Optional.ofNullable(session.get(Book.class, idOfRud));
        assertTrue(actualBook.isEmpty());
    }

    @AfterEach
    public void closeSessions(){
        session.close();
        sessionFactory.close();
    }
}