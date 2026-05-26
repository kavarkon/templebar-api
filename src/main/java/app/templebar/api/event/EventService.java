package app.templebar.api.event;

import app.templebar.api.common.exception.EventNotFoundException;
import app.templebar.api.event.dto.CreateEventRequest;
import app.templebar.api.event.dto.EventResponse;
import app.templebar.api.event.dto.UpdateEventRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAllByOrderByScheduledAtAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EventResponse createEvent(CreateEventRequest request) {

        Event event = new Event();

        event.setTitle(request.title());
        event.setImage(request.image());
        event.setDescription(request.description());
        event.setScheduledAt(request.scheduledAt());

        Event savedEvent = eventRepository.save(event);

        return toResponse(savedEvent);
    }

    @Transactional
    public EventResponse updateEvent(
            Long id,
            UpdateEventRequest request
    ) {

        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        if (request.title() != null) {
            event.setTitle(request.title());
        }

        if (request.image() != null) {
            event.setImage(request.image());
        }

        if (request.description() != null) {
            event.setDescription(request.description());
        }

        if (request.scheduledAt() != null) {
            event.setScheduledAt(request.scheduledAt());
        }

        return toResponse(event);
    }

    public void deleteEvent(Long id) {

        boolean exists = eventRepository.existsById(id);

        if (!exists) {
            throw new EventNotFoundException();
        }

        eventRepository.deleteById(id);
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getImage(),
                event.getDescription(),
                event.getScheduledAt()
        );
    }
}
