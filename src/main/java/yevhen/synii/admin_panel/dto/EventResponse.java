package yevhen.synii.admin_panel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    @JsonProperty(value = "event_id")
    private Long eventId;
    @JsonProperty(value = "event_name")
    private String eventName;
    @JsonProperty(value = "event_description")
    private String eventDescription;
    @JsonProperty(value = "event_link")
    private String eventLink;
    @JsonProperty(value = "event_date_time")
    private Timestamp eventDateTime;
    private String facilitator;
    @JsonProperty(value = "event_attendee_list")
    private List<String> attendeeEmails;

}
