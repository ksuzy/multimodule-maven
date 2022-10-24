package com.dmdev.integration.filter.querydsl;

import com.dmdev.dao.predicates.QPredicate;
import com.dmdev.dto.UserFilter;
import com.dmdev.entity.QUser;
import com.dmdev.entity.User;
import com.dmdev.util.HibernateTestUtil;
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

import java.util.List;

import static com.dmdev.util.HibernateTestUtil.sessionFactory;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class UserIT {

    private Session session;

    @BeforeAll
    public static void initialization() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
        TestDataImporter.importData(sessionFactory);
    }

    @AfterAll
    public static void finish() {
        sessionFactory.close();
    }

    @BeforeEach
    public void prepareUserTable() {
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    @AfterEach
    public void closeSessions() {
        session.getTransaction().rollback();
        session.close();
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
                .setHint(GraphSemantic.LOAD.getJpaHintName(), entityGraph)
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
                .setHint(GraphSemantic.LOAD.getJpaHintName(), entityGraph)
                .fetch();
    }
}
