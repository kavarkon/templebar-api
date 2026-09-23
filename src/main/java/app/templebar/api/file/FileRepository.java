package app.templebar.api.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface FileRepository
        extends JpaRepository<File, Long> {

    @Query("""
            select storedFile
            from File storedFile
            where storedFile.createdAt < :createdBefore
              and not exists (
                  select event.id
                  from Event event
                  where event.file = storedFile
              )
            """)
    List<File> findOrphanFilesCreatedBefore(
            @Param("createdBefore")
            OffsetDateTime createdBefore
    );
}
