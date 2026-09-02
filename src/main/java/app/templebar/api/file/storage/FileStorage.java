package app.templebar.api.file.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorage {

    String save(MultipartFile file) throws IOException;
}
