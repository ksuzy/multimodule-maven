package com.dmdev.config;

import com.dmdev.database.pool.ConnectionPool;
import com.dmdev.util.HibernateTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "com.dmdev",
        useDefaultFilters = false,
        includeFilters = {
                @Filter(type = FilterType.ANNOTATION, value = Component.class)
        })
public class ApplicationTestConfiguration {

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(HibernateTestUtil.buildSessionFactory());
    }

    @Bean
    public EntityManager entityManager(@Autowired ConnectionPool connectionPool) {
        return connectionPool.sessionFactory().openSession();
    }
}
