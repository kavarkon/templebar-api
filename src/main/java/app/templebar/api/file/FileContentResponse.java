package app.templebar.api.file;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

public record FileContentResponse(
        Resource resource,
        MediaType mediaType
) {
}
