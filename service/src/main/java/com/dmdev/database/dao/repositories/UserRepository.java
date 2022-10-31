package com.dmdev.database.dao.repositories;

import com.dmdev.database.dao.predicates.QPredicate;
import com.dmdev.database.entity.QUser;
import com.dmdev.database.entity.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.hibernate.graph.GraphSemantic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserRepository extends BaseRepository<Integer, User> {

    @Autowired
    public UserRepository(EntityManager entityManager) {
        super(QUser.user, User.class, entityManager);
    }

    public List<User> findAllWithGraphs() {
        var graph = getEntityManager().createEntityGraph(User.class);
        graph.addAttributeNodes("userDetails", "userAddress");

        return findAll(graph);
    }

    public List<User> findAllByFirstname(String firstname) {
        EntityManager entityManager = getEntityManager();
        var entityGraph = entityManager.createEntityGraph(User.class);
        entityGraph.addAttributeNodes("userDetails", "userAddress");

        Predicate predicate = QPredicate.builder()
                .add(firstname, QUser.user.userDetails.firstname::eq)
                .buildAnd();

        return new JPAQuery<User>(entityManager)
                .select(QUser.user)
                .from(QUser.user)
                .where(predicate)
                .setHint(GraphSemantic.FETCH.getJpaHintName(), entityGraph)
                .fetch();
    }
}

