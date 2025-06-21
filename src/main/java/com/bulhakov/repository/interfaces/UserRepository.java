package com.bulhakov.repository.interfaces;

import com.bulhakov.model.User;

import java.util.Optional;

public interface UserRepository {
    void delete(String id);

    User save(User user);

    Optional<User> findById(String id);

    Iterable<User> findAll();

    Optional<User> findByExternalId(Long id);
}