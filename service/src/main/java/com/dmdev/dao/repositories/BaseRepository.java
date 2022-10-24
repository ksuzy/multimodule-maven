package com.dmdev.dao.repositories;

import com.dmdev.entity.BaseEntity;
import com.dmdev.entity.User;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public abstract class BaseRepository<K extends Serializable, E extends BaseEntity<K>> implements Repository<K, E> {

    private final EntityPath<E> qClazz;
    private final Class<E> clazz;
    private final EntityManager entityManager;

    @Override
    public E save(E entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public void delete(E entity) {
        entityManager.remove(entity);
        entityManager.flush();
    }

    @Override
    public void update(E entity) {
        entityManager.merge(entity);
    }

    @Override
    public Optional<E> findById(K id) {
        return Optional.ofNullable(entityManager.find(clazz, id));
    }

    @Override
    public List<E> findAll() {
        return new JPAQuery<User>(entityManager)
                .select(qClazz)
                .from(qClazz)
                .fetch();
    }

    @Override
    public List<E> findAll(EntityGraph<E> graph) {
        return new JPAQuery<User>(entityManager)
                .select(qClazz)
                .from(qClazz)
                .setHint(GraphSemantic.LOAD.getJpaHintName(), graph)
                .fetch();
    }
}

