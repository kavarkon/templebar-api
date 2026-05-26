package app.templebar.api.common.exception;

import java.time.OffsetDateTime;

public record ErrorResponse(
        String error,
        String message,
        OffsetDateTime timestamp
) {
}
