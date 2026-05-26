package app.templebar.api.event;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import app.templebar.api.event.dto.CreateEventRequest;
import app.templebar.api.event.dto.EventResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventResponse> getEvents() {
        return eventService.getAllEvents();
    }


    @PostMapping("/events")
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @PatchMapping("/events/{id}")
    public EventResponse updateEvent(
            @PathVariable Long id,
            @RequestBody UpdateEventRequest request
    ) {
        return eventService.updateEvent(id, request);
    }

    @DeleteMapping("/events/{id}")
    public void deleteEvent(
            @PathVariable Long id
    ) {
        eventService.deleteEvent(id);
    }
}
