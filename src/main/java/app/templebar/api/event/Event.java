package app.templebar.api.event;

import app.templebar.api.file.File;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.time.OffsetDateTime;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "file_id",
            unique = true
    )
    private File file;

    private String description;

    private OffsetDateTime scheduledAt;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public File getFile() {
        return file;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScheduledAt(OffsetDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
