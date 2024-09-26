package yevhen.synii.admin_panel.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import yevhen.synii.admin_panel.dto.CreateEventRequest;

public interface EventService {
    ResponseEntity<?> createEvent(CreateEventRequest request, HttpServletRequest servletRequest);
}
