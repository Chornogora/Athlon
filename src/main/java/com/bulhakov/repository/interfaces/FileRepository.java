package com.bulhakov.repository.interfaces;

import java.util.Map;
import java.util.Optional;

public interface FileRepository {

    void storeFile(Long externalUserId, String filename, String fileId);

    Map<String, String> getFilesForUser(Long externalUserId);

    Optional<String> getFileForUser(Long externalUserId, String filename);

    void renameFile(Long telegramUserId, String existingFileName, String filename);

    void deleteFile(Long telegramUserId, String filename);
}
