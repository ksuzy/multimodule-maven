package com.dmdev.integration.filter.querydsl;

import com.dmdev.config.ApplicationTestConfiguration;
import com.dmdev.database.dao.predicates.QPredicate;
import com.dmdev.database.pool.ConnectionPool;
import com.dmdev.dto.UserFilter;
import com.dmdev.database.entity.QUser;
import com.dmdev.database.entity.User;
import com.dmdev.exceptions.SpringContextCloseException;
import com.dmdev.util.TestDataImporter;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.hibernate.Session;
import org.hibernate.graph.GraphSemantic;
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

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class UserIT {

    private static ApplicationContext context;
    private Session session;

    @BeforeAll
    public static void initialization() {
        context = new AnnotationConfigApplicationContext(ApplicationTestConfiguration.class);
        TestDataImporter.importData(context.getBean(ConnectionPool.class).sessionFactory());
    }

    @AfterAll
    public static void finish() {
        try {
            ((Closeable) context).close();
        } catch (IOException e) {
            throw new SpringContextCloseException(e);
        }
    }

    @BeforeEach
    public void prepareUserTable() {
        session = (Session) context.getBean(EntityManager.class);
        session.beginTransaction();
    }

    @AfterEach
    public void closeSessions() {
        session.getTransaction().rollback();
    }

    @Test
    public void findAllTest() {
        List<User> results = findAll(session);
        assertThat(results).hasSize(3);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates", "Steve Jobs", "Sergey Brin");
    }

    @Test
    public void findAllByFirstnameTest() {
        UserFilter filter = UserFilter.builder()
                .firstname("Bill")
                .build();

        List<User> results = findAllByFirstname(session, filter);
        assertThat(results).hasSize(1);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates");
    }


    private List<User> findAll(Session session) {
        var entityGraph = session.createEntityGraph(User.class);
        entityGraph.addAttributeNodes("userDetails", "userAddress");

        return new JPAQuery<User>(session)
                .select(QUser.user)
                .from(QUser.user)
                .setHint(GraphSemantic.FETCH.getJpaHintName(), entityGraph)
                .fetch();
    }


    private List<User> findAllByFirstname(Session session, UserFilter filter) {
        var entityGraph = session.createEntityGraph(User.class);
        entityGraph.addAttributeNodes("userDetails", "userAddress");

        Predicate predicate = QPredicate.builder()
                .add(filter.getFirstname(), QUser.user.userDetails.firstname::eq)
                .buildAnd();

        return new JPAQuery<User>(session)
                .select(QUser.user)
                .from(QUser.user)
                .where(predicate)
                .setHint(GraphSemantic.FETCH.getJpaHintName(), entityGraph)
                .fetch();
    }
}
