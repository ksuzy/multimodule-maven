package com.dmdev.dao.repositories;

import com.dmdev.dao.predicates.QPredicate;
import com.dmdev.entity.Order;
import com.dmdev.entity.OrderProduct;
import com.dmdev.entity.QOrderProduct;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

public class OrderProductRepository extends BaseRepository<Long, OrderProduct> {
    public OrderProductRepository(EntityManager entityManager) {
        super(QOrderProduct.orderProduct, OrderProduct.class, entityManager);
    }

    public void deleteAllByOrders(List<Order> orders) {
        EntityManager entityManager = getEntityManager();
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        Predicate predicate = QPredicate.builder()
                .add(orders, QOrderProduct.orderProduct.order::in)
                .buildAnd();

        jpaQueryFactory.delete(QOrderProduct.orderProduct).where(predicate).execute();
        entityManager.flush();
    }

    public void deleteALlByOrder(Order order) {
        EntityManager entityManager = getEntityManager();
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        Predicate predicate = QPredicate.builder()
                .add(order, QOrderProduct.orderProduct.order::eq)
                .buildAnd();

        jpaQueryFactory.delete(QOrderProduct.orderProduct).where(predicate).execute();
        entityManager.flush();
    }
}

