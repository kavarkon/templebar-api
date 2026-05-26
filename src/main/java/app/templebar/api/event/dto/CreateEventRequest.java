package app.templebar.api.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.time.OffsetDateTime;

public record CreateEventRequest(

        @NotBlank
        String title,

        String image,

        String description,

        @NotNull
        OffsetDateTime scheduledAt
) {
}
