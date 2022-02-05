package com.bulhakov.services;

import com.bulhakov.model.User;

public interface UserService {

    User addUser(User user);

    void deleteUser(User user);

    User updateUser(User user);

    User findUser(String id);

    Iterable<User> findAll();
}
