package app.templebar.api.event.dto;

import java.time.OffsetDateTime;

public record UpdateEventRequest(

        String title,

        Long fileId,

        String description,

        OffsetDateTime scheduledAt
) {
}
