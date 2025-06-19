package com.bulhakov.repository;

import com.bulhakov.model.User;

import java.util.Optional;

public interface UserRepository {
    void delete(User user);

    User save(User user);

    Optional<User> findById(String id);

    Iterable<User> findAll();
}