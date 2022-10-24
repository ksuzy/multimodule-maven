package com.dmdev.dao.repositories;

import com.dmdev.dao.predicates.QPredicate;
import com.dmdev.dto.UserFilter;
import com.dmdev.entity.QUser;
import com.dmdev.entity.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.hibernate.graph.GraphSemantic;

import javax.persistence.EntityManager;
import java.util.List;


public class UserRepository extends BaseRepository<Integer, User>{

    public UserRepository(EntityManager entityManager) {
        super(QUser.user, User.class, entityManager);
    }

    public List<User> findAllWithGraphs(){
        var graph = getEntityManager().createEntityGraph(User.class);
        graph.addAttributeNodes("userDetails", "userAddress");

        return findAll(graph);
    }

    public List<User> findAllByFirstname(UserFilter filter) {
        EntityManager entityManager = getEntityManager();
        var entityGraph = entityManager.createEntityGraph(User.class);
        entityGraph.addAttributeNodes("userDetails", "userAddress");

        Predicate predicate = QPredicate.builder()
                .add(filter.getFirstname(), QUser.user.userDetails.firstname::eq)
                .buildAnd();

        return new JPAQuery<User>(entityManager)
                .select(QUser.user)
                .from(QUser.user)
                .where(predicate)
                .setHint(GraphSemantic.LOAD.getJpaHintName(), entityGraph)
                .fetch();
    }
}

