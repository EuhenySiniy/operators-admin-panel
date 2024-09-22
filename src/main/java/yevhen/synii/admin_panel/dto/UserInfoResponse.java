package yevhen.synii.admin_panel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yevhen.synii.admin_panel.entity.enums.UserRole;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    @JsonProperty(value = "user_id", index = 0)
    private Long id;
    @JsonProperty(index = 1)
    private String email;
    @JsonProperty(index = 2)
    private UserRole role;
    @JsonProperty(value = "full_name", index = 3)
    private String fullName;
    @JsonProperty(value = "profile_photo", index = 4)
    private String profilePhoto;
    @JsonProperty(value = "start_work_at", index = 5)
    private Timestamp startWorkAt;
    @JsonProperty(value = "next_shift_at", index = 6)
    private Timestamp nextShiftAt;
}
