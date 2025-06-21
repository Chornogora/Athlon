package com.bulhakov.services;

import com.bulhakov.model.User;
import com.bulhakov.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User findUser(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findUserByExternalId(Long externalId) {
        return userRepository.findByExternalId(externalId).orElse(null);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
}
