package yevhen.synii.admin_panel.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.dto.CreateEventRequest;
import yevhen.synii.admin_panel.dto.EventResponse;
import yevhen.synii.admin_panel.entity.EventEntity;
import yevhen.synii.admin_panel.entity.UserEntity;
import yevhen.synii.admin_panel.exception.BadRequestException;
import yevhen.synii.admin_panel.exception.UserIsNotFound;
import yevhen.synii.admin_panel.repository.EventsRepo;
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.EventService;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String TEMPORARY_LINK_ON_MEET = "https://meet.google.com/ykj-ejxi-nkz";
    private final EventsRepo eventsRepo;
    private final UsersRepo userRepo;
    private final JwtServiceImpl jwtServiceImpl;

    @Override
    public ResponseEntity<?> createEvent(CreateEventRequest request, HttpServletRequest servletRequest) {
        if(
                request.getEventName().isBlank() ||
                request.getEventDescription().isBlank() ||
                request.getEventDateTime() == null
        ) {
            throw new BadRequestException("Required parameter(s) is(are) absent");
        }
        if(isEventPast(request.getEventDateTime())) {
            throw new BadRequestException("The event has already passed");
        }
        final String authHeader = servletRequest.getHeader("Authorization");
        final String jwt;
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        jwt = authHeader.substring(7);
        UserEntity userEntity = userRepo.findById(jwtServiceImpl.extractId(jwt))
                .orElseThrow(() -> new UserIsNotFound("User is not found exception"));
        Set<UserEntity> attendees = new HashSet<>();
        attendees.add(userEntity);
        EventEntity savedEvent = eventsRepo.save(EventEntity.builder()
                .eventName(request.getEventName())
                .eventDescription(request.getEventDescription())
                .eventLink(TEMPORARY_LINK_ON_MEET)
                .eventDateTime(request.getEventDateTime())
                .userEntity(userEntity)
                .users(attendees)
                .created_at(new Timestamp(System.currentTimeMillis()))
                .updated_at(new Timestamp(System.currentTimeMillis()))
                .build());
        EventResponse response = EventResponse.builder()
                .eventId(savedEvent.getId())
                .eventName(savedEvent.getEventName())
                .eventDescription(savedEvent.getEventDescription())
                .eventLink(savedEvent.getEventLink())
                .eventDateTime(savedEvent.getEventDateTime())
                .facilitator(userEntity.getEmail())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public List<EventResponse> getEventsByUserId(Long id) {
        List<EventEntity> eventsResponses = eventsRepo.getUserEventsByUserId(id);
        return eventsResponses.stream()
                .map(e -> EventResponse.builder()
                        .eventId(e.getId())
                        .eventName(e.getEventName())
                        .eventDescription(e.getEventDescription())
                        .eventLink(e.getEventLink())
                        .eventDateTime(e.getEventDateTime())
                        .facilitator(e.getUserEntity().getFirstName() + " " + e.getUserEntity().getLastName())
                        .build())
                .toList();
    }

    public boolean isEventPast(Timestamp dateTime) {
        return dateTime.before(new Timestamp(System.currentTimeMillis()));
    }
}
