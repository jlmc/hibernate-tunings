package io.costax.hibernatetunning;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

/**
 * File Content Reader.
 * Read a file Content of a existing file in the classpath
 */
public final class FileContentReader {

    private FileContentReader() {
    }

    public static String readAllText(String resourcePath) {
        try {
            //String resourcePath = "payloads/example.json";
            ClassLoader classLoader = FileContentReader.class.getClassLoader();
            File file = new File(Objects.requireNonNull(classLoader.getResource(resourcePath)).getFile());

            //Read File Content
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static byte[] readAllBytes(String resourcePath) {
        try {
            //String resourcePath = "payloads/example.json";
            ClassLoader classLoader = FileContentReader.class.getClassLoader();
            File file = new File(Objects.requireNonNull(classLoader.getResource(resourcePath)).getFile());

            //Read File Content
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public static void main(String[] args) {
        final UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
    }
}