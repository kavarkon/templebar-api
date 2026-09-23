package app.templebar.api.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FileCleanupService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    FileCleanupService.class
            );

    private final FileRepository fileRepository;
    private final FileService fileService;
    private final Duration orphanMaxAge;

    public FileCleanupService(
            FileRepository fileRepository,
            FileService fileService,
            @Value("${storage.cleanup.orphan-max-age}")
            Duration orphanMaxAge
    ) {
        this.fileRepository = fileRepository;
        this.fileService = fileService;
        this.orphanMaxAge = orphanMaxAge;
    }

    @Scheduled(
            fixedDelayString =
                    "${storage.cleanup.interval}"
    )
    public void deleteOrphanFiles() {

        OffsetDateTime createdBefore =
                OffsetDateTime.now()
                        .minus(orphanMaxAge);

        List<File> orphanFiles =
                fileRepository
                        .findOrphanFilesCreatedBefore(
                                createdBefore
                        );

        int deletedFiles = 0;

        for (File file : orphanFiles) {
            try {
                fileService.delete(file);
                deletedFiles++;
            } catch (
                    DataIntegrityViolationException
                            exception
            ) {
                log.info(
                        "File {} was not deleted because it is already in use",
                        file.getId()
                );
            } catch (RuntimeException exception) {
                log.error(
                        "Failed to delete orphan file {}",
                        file.getId(),
                        exception
                );
            }
        }

        if (deletedFiles > 0) {
            log.info(
                    "Deleted {} orphan files",
                    deletedFiles
            );
        }
    }
}
