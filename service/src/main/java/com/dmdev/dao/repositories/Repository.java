package com.dmdev.dao.repositories;

import com.dmdev.entity.BaseEntity;

import javax.persistence.EntityGraph;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface Repository<K extends Serializable, E extends BaseEntity<K>> {

    E save(E entity);

    default void deleteById(K id) {
        Optional<E> maybeEntity = findById(id);
        if (maybeEntity.isPresent()){
            delete(maybeEntity.get());
        } else {
            throw new RuntimeException();
        }
    }

    void delete(E entity);

    void update(E entity);

    Optional<E> findById(K id);

    List<E> findAll();

    List<E> findAll(EntityGraph<E> graph);
}

