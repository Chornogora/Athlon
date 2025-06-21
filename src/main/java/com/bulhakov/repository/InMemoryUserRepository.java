package com.bulhakov.repository;

import com.bulhakov.model.User;
import com.bulhakov.repository.interfaces.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public void delete(String id) {
        users.remove(id);
    }

    @Override
    public User save(User user) {
        if (user.id() == null) {
            String id = UUID.randomUUID().toString();
            user = new User(id, user.externalId(), user.login(), user.username(), user.birthday(), user.banned());
        }
        users.put(user.id(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByExternalId(Long id) {
        return users.values().stream()
                .filter(user -> user.externalId().equals(id))
                .findFirst();
    }

    @Override
    public Iterable<User> findAll() {
        return users.values();
    }
}
