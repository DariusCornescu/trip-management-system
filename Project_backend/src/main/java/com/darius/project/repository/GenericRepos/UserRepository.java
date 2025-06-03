package com.darius.project.repository.GenericRepos;
import com.darius.project.domain.User;

public interface UserRepository extends GenericRepository<Integer, User> {
    User findByUsername(String username);
}
