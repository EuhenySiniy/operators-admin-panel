package yevhen.synii.admin_panel.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yevhen.synii.admin_panel.dto.CreateEventRequest;
import yevhen.synii.admin_panel.dto.EventResponse;
import yevhen.synii.admin_panel.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/create-event")
    public ResponseEntity<?> createEvent(
            @RequestBody CreateEventRequest request,
            HttpServletRequest servletRequest
    ) {
        return eventService.createEvent(request, servletRequest);
    }

    @GetMapping("/get-events/{id}")
    public ResponseEntity<?> getEventsById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(eventService.getEventsByUserId(id), HttpStatus.OK);
    }
}
