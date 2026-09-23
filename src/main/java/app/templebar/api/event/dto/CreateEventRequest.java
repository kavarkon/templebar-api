package app.templebar.api.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CreateEventRequest(

        @NotBlank
        String title,

        Long fileId,

        String description,

        @NotNull
        OffsetDateTime scheduledAt
) {
}
