package com.darius.project.repository;
import com.darius.project.domain.Identifiable;
import com.darius.project.repository.GenericRepos.GenericRepository;
import java.util.HashMap;
import java.util.Iterator;

public class MemoryRepository<ID,T extends Identifiable<ID>> implements GenericRepository<ID,T> {
    protected HashMap<ID,T> data = new HashMap<>();

    @Override
    public void save(ID id, T entity) { data.put(id, entity); }

    @Override
    public void delete(ID id) { data.remove(id); }

    @Override
    public void update(ID id, T updatedEntity) { data.put(id,updatedEntity); }

    @Override
    public T findById(ID id) { return data.get(id); }

    @Override
    public Iterator<T> findAll() { return data.values().iterator(); }

}
