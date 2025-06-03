package com.darius.project.repository.GenericRepos;
import com.darius.project.domain.Identifiable;
import java.util.Iterator;

public interface GenericRepository<ID, T extends Identifiable<ID>> {
    T findById(ID id);
    Iterator<T> findAll();
    void save(ID id,T entity);
    void update(ID id, T updatedEntity);
    void delete(ID id);
}
