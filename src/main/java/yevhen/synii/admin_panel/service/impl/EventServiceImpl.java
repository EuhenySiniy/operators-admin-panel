package yevhen.synii.admin_panel.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.dto.*;
import yevhen.synii.admin_panel.entity.EventEntity;
import yevhen.synii.admin_panel.entity.UserEntity;
import yevhen.synii.admin_panel.exception.BadRequestException;
import yevhen.synii.admin_panel.exception.UserIsNotFound;
import yevhen.synii.admin_panel.repository.EventsRepo;
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.EventService;

import java.sql.Timestamp;
import java.util.ArrayList;
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
                .facilitator(userEntity.getFirstName() + " " + userEntity.getLastName())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getEventsByUserId(HttpServletRequest servletRequest) {
        final String authHeader = servletRequest.getHeader("Authorization");
        final String jwt;
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        jwt = authHeader.substring(7);
        List<EventEntity> eventsResponses = eventsRepo.getUserEventsByUserId(jwtServiceImpl.extractId(jwt));
        return new ResponseEntity<>(eventsResponses.stream()
                .map(e -> EventResponse.builder()
                        .eventId(e.getId())
                        .eventName(e.getEventName())
                        .eventDescription(e.getEventDescription())
                        .eventLink(e.getEventLink())
                        .eventDateTime(e.getEventDateTime())
                        .facilitator(e.getUserEntity().getFirstName() + " " + e.getUserEntity().getLastName())
                        .build())
                .toList(),
                HttpStatus.OK);
    }

    @Override
    public AttendeesInfoResponse assignAttendee(AssignAttendeesRequest attendee) {
        EventEntity event = eventsRepo.getEventById(attendee.getEventId());
        Set<UserEntity> users = new HashSet<>();
        users.add(event.getUserEntity());
        List<UserBioResponse> attendeesBio = new ArrayList<>();
        attendee.getIds().forEach(a -> {
            UserEntity user = userRepo.findById(a).orElseThrow();
            users.add(user);
            attendeesBio.add(UserBioResponse.builder()
                    .fullName(user.getFirstName() + " " + user.getLastName())
                    .email(user.getEmail())
                    .profilePhoto(user.getProfilePhoto())
                    .build());
        }
        );
        event.setUsers(users);
        eventsRepo.save(event);
        return AttendeesInfoResponse.builder()
                .eventId(event.getId())
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .eventLink(event.getEventLink())
                .eventDateTime(event.getEventDateTime())
                .facilitator(event.getUserEntity().getFirstName() + " " + event.getUserEntity().getLastName())
                .attendees(attendeesBio)
                .build();
    }

    public boolean isEventPast(Timestamp dateTime) {
        return dateTime.before(new Timestamp(System.currentTimeMillis()));
    }
}
