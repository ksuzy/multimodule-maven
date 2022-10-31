package com.dmdev.database.dao.repositories;

import com.dmdev.database.dao.predicates.QPredicate;
import com.dmdev.database.entity.Order;
import com.dmdev.database.entity.QOrder;
import com.dmdev.database.entity.fields.Status;
import com.dmdev.exceptions.EntityNotFoundException;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository extends BaseRepository<Long, Order> {

    private final OrderProductRepository orderProductRepository;

    @Autowired
    public OrderRepository(EntityManager entityManager, OrderProductRepository orderProductRepository) {
        super(QOrder.order, Order.class, entityManager);
        this.orderProductRepository = orderProductRepository;
    }

    public List<Order> findAllByUserId(Integer id) {
        EntityManager entityManager = getEntityManager();

        Predicate predicate = QPredicate.builder()
                .add(id, QOrder.order.user.id::eq)
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
            throw new EntityNotFoundException("The order with id = " + id + " does not exist");
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

        orderProductRepository.deleteAllByOrders(orders);

        queryFactory.delete(QOrder.order).where(predicate).execute();
        entityManager.flush();
    }
}