package com.bulhakov.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FormatConvertionService {

    public boolean convertMp3ToOgg(File mp3File, File oggFile) {
        List<String> command = getCmdCommand(mp3File, oggFile);

        int exitCode = -1;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug(line);
                }
            }

            exitCode = process.waitFor();
        } catch (IOException e) {
            log.error("Failed to run ffmpeg command", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error("FFmpeg command execution was interrupted", e);
            throw new RuntimeException(e);
        }

        if (exitCode == 0) {
            log.info("FFmpeg command executed successfully.");
            return true;
        } else {
            log.error("FFmpeg command failed with exit code: " + exitCode);
            return false;
        }
    }

    private static List<String> getCmdCommand(File mp3File, File oggFile) {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(mp3File.getAbsolutePath());
        command.add("-vn"); // Disable video recording (if any in the MP3, though unlikely)
        command.add("-acodec");
        command.add("libvorbis"); // Specify the OGG Vorbis audio codec
        command.add(oggFile.getAbsolutePath());
        return command;
    }
}
