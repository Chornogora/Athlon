package com.bulhakov.repository;

import com.bulhakov.repository.interfaces.FileRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryFileRepository implements FileRepository {

    private final Map<String, Map<String, String>> files = new HashMap<>();

    @Override
    public void storeFile(String username, String filename, String fileId) {
        if (!files.containsKey(username)) {
            files.put(username, new HashMap<>());
        }

        Map<String, String> userFiles = files.get(username);
        userFiles.put(filename, fileId);
    }

    @Override
    public Map<String, String> getFilesForUser(String username) {
        return files.getOrDefault(username, new HashMap<>());
    }
}
