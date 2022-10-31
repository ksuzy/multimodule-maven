package com.dmdev.util;

import com.dmdev.database.entity.Author;
import com.dmdev.database.entity.Book;
import com.dmdev.database.entity.Order;
import com.dmdev.database.entity.OrderProduct;
import com.dmdev.database.entity.User;
import com.dmdev.database.entity.UserAddress;
import com.dmdev.database.entity.UserDetails;

import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateUtil {

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
