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
public class AttendeesInfoResponse {
    @JsonProperty(value = "event_id", index = 0)
    private Long eventId;
    @JsonProperty(value = "event_name", index = 1)
    private String eventName;
    @JsonProperty(value = "event_description" , index = 2)
    private String eventDescription;
    @JsonProperty(value = "event_link", index = 3)
    private String eventLink;
    @JsonProperty(value = "event_date_time", index = 4)
    private Timestamp eventDateTime;
    @JsonProperty(value = "facilitator", index = 5)
    private String facilitator;
    @JsonProperty(value = "attendees", index = 6)
    private List<UserBioResponse> attendees;
}
