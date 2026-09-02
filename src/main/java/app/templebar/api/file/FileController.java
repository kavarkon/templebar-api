package app.templebar.api.file;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final FileContentService fileContentService;

    public FileController(
            FileService fileService,
            FileContentService fileContentService
    ) {
        this.fileService = fileService;
        this.fileContentService = fileContentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FileResponse upload(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        File uploadedFile = fileService.upload(file);

        return new FileResponse(
                uploadedFile.getId()
        );
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> getContent(
            @PathVariable Long id
    ) throws IOException {

        FileContentResponse file = fileContentService.getContent(id);

        return ResponseEntity.ok()
                .contentType(file.mediaType())
                .body(file.resource());
    }
}
