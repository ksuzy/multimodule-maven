package com.dmdev.database.pool;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;

public record ConnectionPool(SessionFactory sessionFactory) {

    @PreDestroy
    public void destroy() {
        sessionFactory.close();
    }

    public EntityManager getEntityManager() {
        return getSession();
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
