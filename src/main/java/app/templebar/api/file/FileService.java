package app.templebar.api.file;

import app.templebar.api.file.storage.FileStorage;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FileStorage fileStorage;

    public FileService(
            FileRepository fileRepository,
            FileStorage fileStorage
    ) {
        this.fileRepository = fileRepository;
        this.fileStorage = fileStorage;
    }

    @Transactional
    public File upload(MultipartFile multipartFile) throws IOException {

        String path = fileStorage.save(multipartFile);

        File file = new File();
        file.setPath(path);

        return fileRepository.save(file);
    }
}
