package org.unibl.etf.youtubetrimmer.trimmer.service;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.common.util.FileUtils;
import org.unibl.etf.youtubetrimmer.trimmer.properties.TrimmerProperties;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TrimmingService {

    private final TrimmerProperties props;
    private static final String OUTPUT_EXTENSION = "mp4";
    private static final int POLL_TIME = 500;
    private final ConcurrentLinkedQueue<Integer> cancelQueue = new ConcurrentLinkedQueue<>();

    @SneakyThrows
    public Optional<Path> trim(String videoPath, int trimFrom, int trimTo, int jobId) {
        cleanWorkingDir();
        Process process = createTrimProcess(videoPath, trimFrom, trimTo - trimFrom);

        while (process.isAlive()) {
            process.waitFor(POLL_TIME, TimeUnit.MILLISECONDS);
            if (!cancelQueue.isEmpty() && cancelQueue.poll() == jobId) {
                process.destroy();
                return Optional.empty();
            }
        }

        return getTrimmedVideoPath();
    }

    public void cancelJob(int jobId) {
        cancelQueue.add(jobId);
    }

    @SneakyThrows
    private Process createTrimProcess(String videoPath, int trimFrom, int length) {

        String filename = Path.of(videoPath).getFileName().toString()
                .split("\\.")[0] + "." + OUTPUT_EXTENSION;

        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg",
                "-i",
                videoPath,
                "-ss",
                Integer.toString(trimFrom),
                "-t",
                Integer.toString(length),
                "-codec:v",
                "libx264",
                filename);
        builder.redirectError(ProcessBuilder.Redirect.DISCARD);
        builder.directory(new File(props.getWorkingDirectory()));
        return builder.start();
    }

    @SneakyThrows
    private void cleanWorkingDir() {
        FileUtils.cleanDirectory(Path.of(props.getWorkingDirectory()));
    }

    @SneakyThrows
    private Optional<Path> getTrimmedVideoPath() {
        @Cleanup Stream<Path> files = Files.list(Path.of(props.getWorkingDirectory()));
        return files.findFirst();
    }

}
