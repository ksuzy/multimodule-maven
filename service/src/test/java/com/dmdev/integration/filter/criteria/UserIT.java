package com.dmdev.integration.filter.criteria;

import com.dmdev.dao.predicates.CriteriaPredicate;
import com.dmdev.dto.UserFilter;
import com.dmdev.entity.User;
import com.dmdev.entity.UserDetails_;
import com.dmdev.entity.User_;
import com.dmdev.util.HibernateTestUtil;
import com.dmdev.util.TestDataImporter;
import org.hibernate.Session;
import org.hibernate.graph.GraphSemantic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.criteria.Predicate;
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

        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);

        criteria.select(user);

        return session.createQuery(criteria)
                .setHint(GraphSemantic.LOAD.getJpaHintName(), entityGraph)
                .list();
    }


    private List<User> findAllByFirstname(Session session, UserFilter filter) {
        var entityGraph = session.createEntityGraph(User.class);
        entityGraph.addAttributeNodes("userDetails", "userAddress");

        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);

        Predicate[] predicates = CriteriaPredicate.builder()
                .add(user.get(User_.userDetails).get(UserDetails_.FIRSTNAME), filter.getFirstname(), cb)
                .getArray();

        criteria.select(user)
                .where(predicates);

        return session.createQuery(criteria)
                .setHint(GraphSemantic.LOAD.getJpaHintName(), entityGraph)
                .list();
    }
}
