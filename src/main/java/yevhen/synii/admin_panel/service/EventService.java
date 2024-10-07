package yevhen.synii.admin_panel.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import yevhen.synii.admin_panel.dto.AssignAttendeesRequest;
import yevhen.synii.admin_panel.dto.AttendeesInfoResponse;
import yevhen.synii.admin_panel.dto.CreateEventRequest;
import yevhen.synii.admin_panel.dto.EventResponse;

import java.util.List;

public interface EventService {
    ResponseEntity<?> createEvent(CreateEventRequest request, HttpServletRequest servletRequest);

    ResponseEntity<?> getEventsByUserId(HttpServletRequest servletRequest);

    public AttendeesInfoResponse assignAttendee(AssignAttendeesRequest attendee);
}
