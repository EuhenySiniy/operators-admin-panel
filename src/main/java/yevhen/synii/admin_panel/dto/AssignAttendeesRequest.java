package yevhen.synii.admin_panel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignAttendeesRequest {
    @JsonProperty("event_id")
    private Long eventId;
    @JsonProperty("attendee_ids")
    private List<Long> ids;
}
