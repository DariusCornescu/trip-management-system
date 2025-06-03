package com.darius.project.repository.GenericRepos;
import com.darius.project.domain.Reservation;
import java.util.List;

public interface ReservationRepository extends GenericRepository<Integer, Reservation> {
    List<Reservation> findByTripId(Integer tripId);
}
