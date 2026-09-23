package app.templebar.api.file;

import app.templebar.api.common.exception.FileNotFoundException;
import app.templebar.api.common.exception.InvalidFileException;
import app.templebar.api.file.storage.FileStorage;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
public class FileService {

    private static final Logger log =
            LoggerFactory.getLogger(FileService.class);

    private static final long MAX_FILE_SIZE =
            10L * 1024 * 1024;

    private static final String WEBP_CONTENT_TYPE =
            "image/webp";

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_PNG_VALUE,
                    WEBP_CONTENT_TYPE
            );

    private static final byte[] JPEG_SIGNATURE = {
            (byte) 0xFF,
            (byte) 0xD8,
            (byte) 0xFF
    };

    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89,
            (byte) 0x50,
            (byte) 0x4E,
            (byte) 0x47,
            (byte) 0x0D,
            (byte) 0x0A,
            (byte) 0x1A,
            (byte) 0x0A
    };

    private static final byte[] RIFF_SIGNATURE = {
            (byte) 0x52,
            (byte) 0x49,
            (byte) 0x46,
            (byte) 0x46
    };

    private static final byte[] WEBP_SIGNATURE = {
            (byte) 0x57,
            (byte) 0x45,
            (byte) 0x42,
            (byte) 0x50
    };

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
    public File upload(
            MultipartFile multipartFile
    ) throws IOException {

        validate(multipartFile);

        String path = fileStorage.save(multipartFile);

        registerRollbackCleanup(path);

        File file = new File();
        file.setPath(path);

        return fileRepository.save(file);
    }

    public File getById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(FileNotFoundException::new);
    }

    @Transactional
    public void delete(File file) {

        String path = file.getPath();

        fileRepository.delete(file);

        registerCommitCleanup(path);
    }

    private void validate(
            MultipartFile multipartFile
    ) throws IOException {

        if (multipartFile.isEmpty()) {
            throw new InvalidFileException(
                    "File is empty"
            );
        }

        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    "File size exceeds 10 MB"
            );
        }

        String contentType =
                multipartFile.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            throw new InvalidFileException(
                    "Only JPEG, PNG and WebP files are allowed"
            );
        }

        try (InputStream inputStream =
                     multipartFile.getInputStream()) {

            byte[] header =
                    inputStream.readNBytes(12);

            if (!hasValidSignature(
                    contentType,
                    header
            )) {
                throw new InvalidFileException(
                        "File content does not match its content type"
                );
            }
        }
    }

    private boolean hasValidSignature(
            String contentType,
            byte[] header
    ) {

        return switch (contentType) {
            case MediaType.IMAGE_JPEG_VALUE ->
                    matches(header, 0, JPEG_SIGNATURE);

            case MediaType.IMAGE_PNG_VALUE ->
                    matches(header, 0, PNG_SIGNATURE);

            case WEBP_CONTENT_TYPE ->
                    matches(header, 0, RIFF_SIGNATURE)
                            && matches(
                                    header,
                                    8,
                                    WEBP_SIGNATURE
                            );

            default -> false;
        };
    }

    private boolean matches(
            byte[] data,
            int offset,
            byte[] signature
    ) {

        if (data.length < offset + signature.length) {
            return false;
        }

        for (int index = 0;
             index < signature.length;
             index++) {

            if (data[offset + index]
                    != signature[index]) {
                return false;
            }
        }

        return true;
    }

    private void registerRollbackCleanup(
            String path
    ) {

        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager
                .registerSynchronization(
                        new TransactionSynchronization() {

                            @Override
                            public void afterCompletion(
                                    int status
                            ) {

                                if (status
                                        == STATUS_ROLLED_BACK) {
                                    deleteFromStorage(path);
                                }
                            }
                        }
                );
    }

    private void registerCommitCleanup(
            String path
    ) {

        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {

            deleteFromStorage(path);
            return;
        }

        TransactionSynchronizationManager
                .registerSynchronization(
                        new TransactionSynchronization() {

                            @Override
                            public void afterCommit() {
                                deleteFromStorage(path);
                            }
                        }
                );
    }

    private void deleteFromStorage(
            String path
    ) {

        try {
            fileStorage.delete(path);
        } catch (IOException exception) {
            log.error(
                    "Failed to delete file from storage: {}",
                    path,
                    exception
            );
        }
    }
}
