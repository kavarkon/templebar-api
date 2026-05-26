package app.templebar.api.event.dto;

import java.time.OffsetDateTime;

public record UpdateEventRequest(

        String title,

        String image,

        String description,

        OffsetDateTime scheduledAt
) {
}
