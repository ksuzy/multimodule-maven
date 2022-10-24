package com.dmdev.integration;

import com.dmdev.dao.repositories.BookRepository;
import com.dmdev.entity.Author;
import com.dmdev.entity.BaseEntity;
import com.dmdev.entity.Book;
import com.dmdev.util.HibernateTestUtil;
import com.dmdev.util.TestDataImporter;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.dmdev.util.HibernateTestUtil.sessionFactory;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookIT {

    private Book book;
    private Session session;
    private BookRepository repository;
    private static List<BaseEntity> data;

    @BeforeAll
    public static void initialization() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
        data = TestDataImporter.importData(sessionFactory);
    }

    @AfterAll
    public static void finish() {
        sessionFactory.close();
    }

    @BeforeEach
    public void prepareBookTable() {
        session = sessionFactory.getCurrentSession();
        repository = new BookRepository(session);
        Author author = HibernateTestUtil.createAuthorToReadUpdateDelete();
        book = HibernateTestUtil.createBook(author);
        session.beginTransaction();
        session.save(author);
        session.save(book);
        session.flush();
    }

    @Test
    public void createBookTest() {
        Author author = HibernateTestUtil.createAuthorToInsert();
        Book book = HibernateTestUtil.createBook(author);

        session.save(author);
        repository.save(book);

        assertNotNull(book.getId());
    }

    @Test
    public void readBookTest() {
        session.evict(book);
        Optional<Book> maybeBook = repository.findById(book.getId());

        assertFalse(maybeBook.isEmpty());
        assertEquals(book, maybeBook.get());
    }

    @Test
    public void updateBookTest() {
        book.setName("bookAlreadyUpdated");
        repository.update(book);
        session.flush();
        session.evict(book);
        Book actualBook = session.get(Book.class, book.getId());

        assertEquals(book, actualBook);
    }

    @Test
    public void deleteAuthorTest() {
        repository.delete(book);
        Book actualBook = session.get(Book.class, book.getId());

        assertNull(actualBook);
    }

    @Test
    void findAllTest() {
        List<Book> results = repository.findAll();
        assertThat(results).hasSize(11);

        List<String> fullNames = results.stream().map(Book::getName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder(
                "Java. Библиотека профессионала. Том 1",
                "Java. Библиотека профессионала. Том 2",
                "Java SE 8. Вводный курс",
                "7 навыков высокоэффективных людей",
                "Разбуди в себе исполина",
                "Думай и богатей",
                "Богатый папа, бедный папа",
                "Квадрант денежного потока",
                "Как перестать беспокоиться и начать жить",
                "Как завоевывать друзей и оказывать влияние на людей",
                "Chipolino"
        );
    }

    @Test
    void findAllByAuthorTest() {
        Optional<Author> authorForFind = data.stream()
                .filter(Author.class::isInstance)
                .map(Author.class::cast)
                .findFirst();
        assertFalse(authorForFind.isEmpty());

        List<Book> results = repository.findAllByAuthor(authorForFind.get());

        assertThat(results).hasSize(authorForFind.get().getBooks().size());
    }

    @Test
    void findAllByIssueYearTest() {
        List<Book> results = repository.findAllByIssueYear(book.getIssueYear());

        assertThat(results).contains(book);
        assertThat(results).hasSize(2);
    }

    @AfterEach
    void closeSessions() {
        session.getTransaction().rollback();
    }
}