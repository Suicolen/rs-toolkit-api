package api.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

/**
 * For the most part this class will just contain 'helpers' for methods in the Files class
 * These 'helper' methods can be called without wrapping the call inside a try catch block
 * or annotating the method with @SneakyThrows or just making the method throw IOException
 * this class won't contain 'helpers' for all Files methods, just these that are likely to be used more than once
 */
public final class FileUtils {

    public static Path resolveAndCreateDirectory(Path path, String other) {
        Path resolved = path.resolve(other);
        try {
            Files.createDirectories(resolved);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return resolved;
    }

    public static List<Path> getAllFilesInFolder(Path path) {
        try (Stream<Path> paths = Files.list(path)) {
            return paths.toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String readString(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void gzipArchive(Path path, byte[] uncompressed) {
        try (GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(path))) {
            out.write(uncompressed);
            out.finish();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(Path path, byte[] data) {
        try {
            Files.write(path, data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(Path path, Iterable<? extends CharSequence> lines) {
        try {
            Files.write(path, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void delete(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeString(Path path, String str) {
        try {
            Files.writeString(path, str);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private FileUtils() {

    }

}
