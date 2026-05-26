package app.templebar.api.event.dto;

import java.time.OffsetDateTime;

public record EventResponse(
        Long id,
        String title,
        String image,
        String description,
        OffsetDateTime scheduledAt
) {
}
