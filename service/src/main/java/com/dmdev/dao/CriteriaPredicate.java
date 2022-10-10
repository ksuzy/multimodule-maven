package com.dmdev.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CriteriaPredicate {

    public final List<Predicate> predicates = new ArrayList<>();

    public static CriteriaPredicate builder() {
        return new CriteriaPredicate();
    }

    public <T> CriteriaPredicate add(Expression<?> expression, T object, CriteriaBuilder cb) {
        if (object != null) {
            predicates.add(cb.equal(expression, object));
        }
        return this;
    }

    public Predicate[] getArray() {
        return predicates.toArray(Predicate[]::new);
    }

    public List<Predicate> getPredicates() {
        return predicates;
    }
}
