package com.darius.project.repository.ORM;
import com.darius.project.domain.Trip;
import com.darius.project.repository.GenericRepos.TripRepository;
import org.hibernate.*;
import org.hibernate.query.*;
import org.slf4j.*;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class HibernateTripRepository implements TripRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateTripRepository.class);
    private final SessionFactory sessionFactory;

    public HibernateTripRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        LOGGER.info("HibernateTripRepository initialized");
    }

    @Transactional(readOnly = true)
    public Trip findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Trip.class, id);
        }
    }

    @Transactional(readOnly = true)
    public Iterator<Trip> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<Trip> trips = session.createQuery("FROM Trip", Trip.class).list();
            return trips.iterator();
        }
    }

    @Transactional
    public void save(Integer id, Trip entity) {
        entity.setId(id);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    @Transactional
    public void update(Integer id, Trip entity) {
        entity.setId(id);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            tx.commit();
        }
    }

    @Transactional
    public void delete(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Trip t = session.get(Trip.class, id);
            if (t != null) session.remove(t);
            tx.commit();
        }
    }

    @Transactional(readOnly = true)
    public List<Trip> findByAttractionAndTime(String attraction, String startTime, String endTime) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Trip WHERE attractionName LIKE :attr " + "AND SUBSTRING(departureTime,1,5) BETWEEN :start AND :end";
            Query<Trip> q = session.createQuery(hql, Trip.class);
            q.setParameter("attr", "%" + attraction + "%");
            q.setParameter("start", startTime);
            q.setParameter("end", endTime);
            return q.list();
        }
    }

    @Transactional(readOnly = true)
    public List<Trip> findAllByAttractionContaining(String partial) {
        try (Session session = sessionFactory.openSession()) {
            Query<Trip> q = session.createQuery(
                    "FROM Trip WHERE attractionName LIKE :p", Trip.class);
            q.setParameter("p", "%" + partial + "%");
            return q.list();
        }
    }
}
