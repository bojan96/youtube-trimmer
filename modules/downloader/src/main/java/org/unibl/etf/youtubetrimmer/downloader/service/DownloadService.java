package org.unibl.etf.youtubetrimmer.downloader.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.downloader.properties.DownloaderProperties;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DownloadService {

    private static final String OUTPUT_FILENAME_FORMAT = "%(id)s";
    private static final int POLL_TIME = 500;
    private final DownloaderProperties downloadProps;

    @SneakyThrows
    public Optional<Path> download(String videoUrl) {
        //cleanWorkingDir();
        Process process = createDownloadProcess(videoUrl);

        while (process.isAlive()) {
            process.waitFor(POLL_TIME, TimeUnit.MILLISECONDS);
        }

        return getDownloadedVideoPath();
    }

    @SneakyThrows
    private Process createDownloadProcess(String videoUrl) {
        ProcessBuilder builder = new ProcessBuilder("youtube-dl", "-o",
                OUTPUT_FILENAME_FORMAT, videoUrl);
        builder.directory(new File(downloadProps.getWorkingDirectory()));
        return builder.start();
    }

    @SneakyThrows
    private Optional<Path> getDownloadedVideoPath() {
        @Cleanup Stream<Path> files = Files.list(Path.of(downloadProps.getWorkingDirectory()));
        return files.findFirst();
    }

    @SneakyThrows
    private void cleanWorkingDir() {
        @Cleanup Stream<Path> files = Files.list(Path.of(downloadProps.getWorkingDirectory()));
        files.forEach(file -> {
            try {
                Files.delete(file);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }


}
