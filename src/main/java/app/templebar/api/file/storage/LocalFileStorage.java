package app.templebar.api.file.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class LocalFileStorage implements FileStorage {

    private final Path uploadDirectory;

    public LocalFileStorage(
            @Value("${storage.upload-directory}") String uploadDirectory
    ) {
        this.uploadDirectory = Path.of(uploadDirectory);
    }

    @Override
    public String save(MultipartFile file) throws IOException {

        Files.createDirectories(uploadDirectory);

        String originalFilename = file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null) {
            int index = originalFilename.lastIndexOf('.');

            if (index >= 0) {
                extension = originalFilename.substring(index);
            }
        }

        String filename = UUID.randomUUID() + extension;

        Path target = uploadDirectory.resolve(filename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return filename;
    }
}
