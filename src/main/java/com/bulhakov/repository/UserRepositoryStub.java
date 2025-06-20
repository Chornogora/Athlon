package com.bulhakov.repository;

import com.bulhakov.model.User;
import com.bulhakov.repository.interfaces.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryStub implements UserRepository {
    @Override
    public void delete(User user) {

    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        return null;
    }
}
