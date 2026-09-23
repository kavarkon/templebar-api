package app.templebar.api.event;

import app.templebar.api.common.exception.EventNotFoundException;
import app.templebar.api.event.dto.CreateEventRequest;
import app.templebar.api.event.dto.EventResponse;
import app.templebar.api.event.dto.UpdateEventRequest;
import app.templebar.api.file.File;
import app.templebar.api.file.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final FileService fileService;

    @Transactional
    public List<EventResponse> getAllEvents() {
        return eventRepository
                .findAllByOrderByScheduledAtAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EventResponse createEvent(
            CreateEventRequest request
    ) {

        Event event = new Event();

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setScheduledAt(request.scheduledAt());

        if (request.fileId() != null) {
            File file =
                    fileService.getById(request.fileId());

            event.setFile(file);
        }

        Event savedEvent =
                eventRepository.save(event);

        return toResponse(savedEvent);
    }

    @Transactional
    public EventResponse updateEvent(
            Long id,
            UpdateEventRequest request
    ) {

        Event event = eventRepository.findById(id)
                .orElseThrow(
                        EventNotFoundException::new
                );

        if (request.title() != null) {
            event.setTitle(request.title());
        }

        if (request.fileId() != null) {
            replaceFile(
                    event,
                    request.fileId()
            );
        }

        if (request.description() != null) {
            event.setDescription(
                    request.description()
            );
        }

        if (request.scheduledAt() != null) {
            event.setScheduledAt(
                    request.scheduledAt()
            );
        }

        return toResponse(event);
    }

    @Transactional
    public void deleteEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(
                        EventNotFoundException::new
                );

        File file = event.getFile();

        eventRepository.delete(event);
        eventRepository.flush();

        if (file != null) {
            fileService.delete(file);
        }
    }

    private void replaceFile(
            Event event,
            Long newFileId
    ) {

        File oldFile = event.getFile();

        if (oldFile != null
                && oldFile.getId().equals(newFileId)) {
            return;
        }

        File newFile =
                fileService.getById(newFileId);

        event.setFile(newFile);

        eventRepository.flush();

        if (oldFile != null) {
            fileService.delete(oldFile);
        }
    }

    private EventResponse toResponse(
            Event event
    ) {

        String imageUrl = null;

        if (event.getFile() != null) {
            imageUrl =
                    "/files/"
                            + event.getFile().getId()
                            + "/content";
        }

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                imageUrl,
                event.getDescription(),
                event.getScheduledAt()
        );
    }
}
