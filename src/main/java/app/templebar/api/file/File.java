package app.templebar.api.file;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt =
            OffsetDateTime.now();

    protected File() {
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
