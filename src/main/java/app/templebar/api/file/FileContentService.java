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

@Service
public class FileContentService {

    private final FileRepository fileRepository;
    private final Path uploadDirectory;

    public FileContentService(
            FileRepository fileRepository,
            @Value("${storage.upload-directory}") String uploadDirectory
    ) {
        this.fileRepository = fileRepository;
        this.uploadDirectory = Path.of(uploadDirectory);
    }

    public FileContentResponse getContent(Long id) throws IOException {

        File file = fileRepository.findById(id)
                .orElseThrow(FileNotFoundException::new);

        Path path = uploadDirectory.resolve(file.getPath());

        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }

        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException exception) {
            throw new IOException(exception);
        }

        String contentType = Files.probeContentType(path);

        MediaType mediaType;

        if (contentType != null) {
            mediaType = MediaType.parseMediaType(contentType);
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return new FileContentResponse(
                resource,
                mediaType
        );
    }
}
