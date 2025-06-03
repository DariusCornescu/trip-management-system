package com.darius.project.repository.GenericRepos;
import com.darius.project.domain.Trip;
import java.util.List;

public interface TripRepository extends GenericRepository<Integer, Trip> {
    List<Trip> findByAttractionAndTime(String attraction, String startTime, String endTime);
    List<Trip> findAllByAttractionContaining(String partial);
}