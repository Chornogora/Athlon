package com.bulhakov.repository;

import com.bulhakov.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByUserId(String userId);

    Optional<File> findByUserIdAndFileName(String userId, String filename);
}
