package yevhen.synii.admin_panel.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity extends BaseEntity{
    @Column(name = "event_name")
    private String eventName;
    @Column(name = "event_description")
    private String eventDescription;
    @Column(name = "event_link")
    private String eventLink;
    @Column(name = "event_date_time")
    private Timestamp eventDateTime;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facilitator_id")
    private UserEntity userEntity;
    @Column(name = "attendee_emails")
    private List<String> attendeeEmails;
}
