package com.dmdev.database.dao.repositories;

import com.dmdev.database.dao.predicates.QPredicate;
import com.dmdev.database.entity.Order;
import com.dmdev.database.entity.OrderProduct;
import com.dmdev.database.entity.QOrderProduct;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class OrderProductRepository extends BaseRepository<Long, OrderProduct> {

    @Autowired
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