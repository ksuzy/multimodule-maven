package com.dmdev.integration;

import com.dmdev.config.ApplicationTestConfiguration;
import com.dmdev.database.dao.repositories.AuthorRepository;
import com.dmdev.database.entity.Author;
import com.dmdev.database.entity.BaseEntity;
import com.dmdev.database.pool.ConnectionPool;
import com.dmdev.exceptions.SpringContextCloseException;
import com.dmdev.util.HibernateTestUtil;
import com.dmdev.util.TestDataImporter;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthorIT {

    private static ApplicationContext context;

    private Author rudAuthor;
    private Session session;
    private AuthorRepository repository;


    @BeforeAll
    public static void initialization() {
        context = new AnnotationConfigApplicationContext(ApplicationTestConfiguration.class);
        TestDataImporter.importData(context.getBean(ConnectionPool.class).sessionFactory());
    }

    @AfterAll
    static void finish() {
        try {
            ((Closeable) context).close();
        } catch (IOException e) {
            throw new SpringContextCloseException(e);
        }
    }

    @BeforeEach
    public void prepareAuthorTable() {
        session = (Session) context.getBean(EntityManager.class);
        repository = context.getBean(AuthorRepository.class);
        rudAuthor = HibernateTestUtil.createAuthorToReadUpdateDelete();
        session.beginTransaction();
        session.save(rudAuthor);
        session.flush();
    }

    @AfterEach
    void afterTests() {
        session.getTransaction().rollback();
    }


    @Test
    public void createAuthorTest() {
        Author cAuthor = HibernateTestUtil.createAuthorToInsert();

        repository.save(cAuthor);

        assertNotNull(cAuthor.getId());
    }

    @Test
    public void readAuthorTest() {
        session.evict(rudAuthor);
        Optional<Author> maybeAuthor = repository.findById(rudAuthor.getId());

        assertFalse(maybeAuthor.isEmpty());
        assertEquals(rudAuthor, maybeAuthor.get());
    }

    @Test
    public void updateAuthorTest() {
        rudAuthor.setFirstname("IvanAlreadyUpdated");
        repository.update(rudAuthor);
        session.flush();
        session.evict(rudAuthor);
        Author actualAuthor = session.get(Author.class, rudAuthor.getId());

        assertEquals(rudAuthor, actualAuthor);
    }

    @Test
    public void deleteAuthorTest() {
        repository.delete(rudAuthor);

        Author actualAuthor = session.get(Author.class, rudAuthor.getId());
        assertNull(actualAuthor);
    }

    @Test
    void findAllTest() {
        List<Author> results = repository.findAll();
        assertThat(results).hasSize(8);

        List<String> fullNames = results.stream().map(Author::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder(
                "Кей Хорстманн",
                "Гари Корнелл",
                "Стивен Кови",
                "Тони Роббинс",
                "Наполеон Хилл",
                "Роберт Кийосаки",
                "Дейл Карнеги",
                "IvanDeleteReadUpdate Ivanov");
    }
}