package com.dmdev.util;

import com.dmdev.entity.Author;
import com.dmdev.entity.Book;
import com.dmdev.entity.Order;
import com.dmdev.entity.OrderProduct;
import com.dmdev.entity.User;
import com.dmdev.entity.UserAddress;
import com.dmdev.entity.UserDetails;

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateUtil {

    public static SessionFactory sessionFactory;
    public static SessionFactory buildSessionFactory() {
        Configuration configuration = buildConfiguration();
        configuration.configure();

        return configuration.buildSessionFactory();
    }

    public static Configuration buildConfiguration() {
        Configuration configuration = new Configuration();

        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.addAnnotatedClass(Author.class);
        configuration.addAnnotatedClass(Book.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.addAnnotatedClass(OrderProduct.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(UserDetails.class);
        configuration.addAnnotatedClass(UserAddress.class);

        return configuration;
    }
}
