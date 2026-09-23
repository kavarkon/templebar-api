package app.templebar.api.file;

import app.templebar.api.common.exception.FileNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

@Service
public class FileContentService {

    private static final MediaType WEBP_MEDIA_TYPE =
            MediaType.parseMediaType("image/webp");

    private final FileRepository fileRepository;
    private final Path uploadDirectory;

    public FileContentService(
            FileRepository fileRepository,
            @Value("${storage.upload-directory}") String uploadDirectory
    ) {
        this.fileRepository = fileRepository;
        this.uploadDirectory = Path.of(uploadDirectory)
                .toAbsolutePath()
                .normalize();
    }

    public FileContentResponse getContent(Long id) throws IOException {

        File file = fileRepository.findById(id)
                .orElseThrow(FileNotFoundException::new);

        Path path = uploadDirectory
                .resolve(file.getPath())
                .normalize();

        if (!path.startsWith(uploadDirectory)
                || !Files.isRegularFile(path)
                || !Files.isReadable(path)) {

            throw new FileNotFoundException();
        }

        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException exception) {
            throw new IOException(exception);
        }

        return new FileContentResponse(
                resource,
                getMediaType(path)
        );
    }

    private MediaType getMediaType(Path path) {

        String filename = path
                .getFileName()
                .toString()
                .toLowerCase(Locale.ROOT);

        if (filename.endsWith(".jpg")
                || filename.endsWith(".jpeg")) {

            return MediaType.IMAGE_JPEG;
        }

        if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }

        if (filename.endsWith(".webp")) {
            return WEBP_MEDIA_TYPE;
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
