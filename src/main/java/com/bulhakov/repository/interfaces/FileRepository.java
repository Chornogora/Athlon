package com.bulhakov.repository.interfaces;

import java.util.Map;

public interface FileRepository {

    void storeFile(String username, String filename, String fileId);

    Map<String, String> getFilesForUser(String username);
}
