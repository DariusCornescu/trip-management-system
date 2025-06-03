package com.darius.project.repository.ORM;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.*;

public class HibernateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try{
            LOGGER.info("Creating Hibernate SessionFactory");
            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(com.darius.project.domain.Trip.class)
                    .buildSessionFactory();
        }catch(Throwable ex){
            LOGGER.error("Initial SessionFactory creation failed: {}", String.valueOf(ex));
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}