package app.templebar.api.file.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class LocalFileStorage implements FileStorage {

    private static final String WEBP_CONTENT_TYPE = "image/webp";

    private final Path uploadDirectory;

    public LocalFileStorage(
            @Value("${storage.upload-directory}") String uploadDirectory
    ) {
        this.uploadDirectory = Path.of(uploadDirectory)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public String save(MultipartFile file) throws IOException {

        Files.createDirectories(uploadDirectory);

        String extension = getExtension(file);
        String filename = UUID.randomUUID() + extension;

        Path target = resolvePath(filename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    target
            );
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(target);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }

        return filename;
    }

    @Override
    public void delete(String path) throws IOException {

        Path target = resolvePath(path);

        Files.deleteIfExists(target);
    }

    private String getExtension(MultipartFile file) throws IOException {

        String contentType = file.getContentType();

        if (MediaType.IMAGE_JPEG_VALUE.equals(contentType)) {
            return ".jpg";
        }

        if (MediaType.IMAGE_PNG_VALUE.equals(contentType)) {
            return ".png";
        }

        if (WEBP_CONTENT_TYPE.equals(contentType)) {
            return ".webp";
        }

        throw new IOException("Unsupported file type");
    }

    private Path resolvePath(String filename) throws IOException {

        Path target = uploadDirectory
                .resolve(filename)
                .normalize();

        if (!target.startsWith(uploadDirectory)) {
            throw new IOException("Invalid file path");
        }

        return target;
    }
}
