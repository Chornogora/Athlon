package com.bulhakov.repository;

import com.bulhakov.repository.interfaces.FileRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryFileRepository implements FileRepository {

    //User id to filename to fileId mapping
    private final Map<Long, Map<String, String>> files = new HashMap<>();

    @Override
    public void storeFile(Long externalUserId, String filename, String fileId) {
        if (!files.containsKey(externalUserId)) {
            files.put(externalUserId, new HashMap<>());
        }

        Map<String, String> userFiles = files.get(externalUserId);
        userFiles.put(filename, fileId);
    }

    @Override
    public Map<String, String> getFilesForUser(Long externalUserId) {
        return files.getOrDefault(externalUserId, new HashMap<>());
    }

    @Override
    public Optional<String> getFileForUser(Long externalUserId, String filename) {
        Map<String, String> userFiles = files.getOrDefault(externalUserId, new HashMap<>());
        return Optional.ofNullable(userFiles.get(filename));
    }

}
