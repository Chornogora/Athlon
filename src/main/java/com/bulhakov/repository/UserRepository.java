package com.bulhakov.repository;


import com.bulhakov.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Optional, but good practice for clarity. Spring will detect it anyway.
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByExternalId(Long externalId);
}
