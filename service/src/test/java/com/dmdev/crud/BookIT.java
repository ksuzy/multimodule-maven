package com.dmdev.crud;

import com.dmdev.entity.Author;
import com.dmdev.entity.Book;
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

class BookIT {
    private Author author;
    private Book book;
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
    public void prepareBookTable(){
        session = sessionFactory.openSession();
        author = HibernateTestUtil.createAuthorToReadUpdateDelete();
        book = HibernateTestUtil.createBook(author);
        session.beginTransaction();
        session.save(author);
        session.save(book);
        session.getTransaction().commit();
    }

    @Test
    public void createBookTest(){
        Author author = HibernateTestUtil.createAuthorToInsert();
        Book book = HibernateTestUtil.createBook(author);

        session.beginTransaction();
        session.save(author);
        session.save(book);
        session.getTransaction().commit();

        assertNotNull(book.getId());
    }

    @Test
    public void readBookTest(){
        session.beginTransaction();
        book.getAuthors().forEach(author -> {
            author.getBooks().remove(book);
        });
        session.evict(book);
        Book actualBook = session.get(Book.class, book.getId());
        session.getTransaction().commit();

        assertEquals(book, actualBook);
    }

    @Test
    public void updateBookTest(){
        session.beginTransaction();
        book.setName("bookAlreadyUpdated");
        session.update(book);
        session.flush();
        session.evict(book);
        session.flush();
        Book actualBook = session.get(Book.class, book.getId());

        assertEquals(book, actualBook);
    }

    @Test
    public void deleteAuthorTest(){
        author.getBooks().remove(book);
        session.beginTransaction();
        session.delete(book);
        Book actualBook = session.get(Book.class, book.getId());
        session.getTransaction().commit();

        assertNull(actualBook);
    }

    @AfterEach
    public void closeSessions(){
        session.close();
    }
}