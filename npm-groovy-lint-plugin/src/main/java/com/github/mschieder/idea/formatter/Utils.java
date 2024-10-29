package com.github.mschieder.idea.formatter;

import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

class Utils {
    public static void unzipZippedFileFromResource(final Log log, final InputStream inputStream, final String outputDir) throws IOException {
        final long now = System.nanoTime();
        final Path outputPath = Path.of(outputDir);
        try (ZipInputStream zipStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                final Path entryDestination = outputPath.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryDestination);
                } else {
                    Files.createDirectories(entryDestination.getParent());
                    Files.copy(zipStream, entryDestination);
                }
                zipStream.closeEntry();
            }
        }
        log.debug("unzipped in " + NANOSECONDS.toMillis(System.nanoTime() - now) + " ms");
    }
}