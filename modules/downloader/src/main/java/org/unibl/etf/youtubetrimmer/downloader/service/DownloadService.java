package org.unibl.etf.youtubetrimmer.downloader.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.common.util.FileUtils;
import org.unibl.etf.youtubetrimmer.downloader.properties.DownloaderProperties;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DownloadService {

    private static final String OUTPUT_FILENAME_FORMAT = "%(id)s";
    private static final int POLL_TIME = 500;
    private final DownloaderProperties downloadProps;
    private final ConcurrentLinkedQueue<Integer> cancelQueue = new ConcurrentLinkedQueue<>();

    @SneakyThrows
    public Optional<Path> download(String videoUrl, int jobId) {
        cleanWorkingDir();
        Process process = createDownloadProcess(videoUrl);

        while (process.isAlive()) {
            process.waitFor(POLL_TIME, TimeUnit.MILLISECONDS);
            if (!cancelQueue.isEmpty() && cancelQueue.poll() == jobId) {
                process.destroy();
                return Optional.empty();
            }
        }

        return getDownloadedVideoPath();
    }

    public void cancelDownload(int jobId) {
        cancelQueue.add(jobId);
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
        FileUtils.cleanDirectory(Path.of(downloadProps.getWorkingDirectory()));
    }


}
