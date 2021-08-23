package org.unibl.etf.youtubetrimmer.common.util;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {
    public static void cleanDirectory(Path directory) throws IOException {

        if(!Files.isDirectory(directory))
            throw new IllegalArgumentException(String.format("%s is not a directory", directory));

        @Cleanup Stream<Path> files = Files.list(directory);
        files.forEach(file -> {
            try {
                Files.delete(file);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
