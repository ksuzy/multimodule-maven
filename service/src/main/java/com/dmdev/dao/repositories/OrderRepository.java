package com.dmdev.dao.repositories;

import com.dmdev.dao.predicates.QPredicate;
import com.dmdev.entity.Order;
import com.dmdev.entity.QOrder;
import com.dmdev.entity.QUser;
import com.dmdev.entity.User;
import com.dmdev.entity.fields.Status;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderRepository extends BaseRepository<Long, Order> {
    public OrderRepository(EntityManager entityManager) {
        super(QOrder.order, Order.class, entityManager);
    }

    public List<Order> findAllByUser(User user) {
        EntityManager entityManager = getEntityManager();

        Predicate predicate = QPredicate.builder()
                .add(user, QUser.user::eq)
                .buildAnd();

        return new JPAQuery<Order>(entityManager)
                .select(QOrder.order)
                .from(QOrder.order)
                .where(predicate)
                .fetch();
    }

    public void updateStatusById(Long id, Status status) {
        Optional<Order> maybeOrder = findById(id);
        if (maybeOrder.isEmpty()) {
            throw new RuntimeException();
        }
        maybeOrder.get().setStatus(status);
        update(maybeOrder.get());
    }

    public void deleteOrdersMadeEarlierDate(LocalDateTime date) {
        EntityManager entityManager = getEntityManager();
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        Predicate predicate = QPredicate.builder()
                .add(date, QOrder.order.createdAt::before)
                .buildAnd();

        List<Order> orders = new JPAQuery<Order>(entityManager)
                .select(QOrder.order)
                .from(QOrder.order)
                .where(predicate)
                .fetch();

        new OrderProductRepository(entityManager).deleteAllByOrders(orders);

        queryFactory.delete(QOrder.order).where(predicate).execute();
        entityManager.flush();
    }
}
