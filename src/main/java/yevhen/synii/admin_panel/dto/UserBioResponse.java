package yevhen.synii.admin_panel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBioResponse {
    @JsonProperty(value = "full_name")
    private String fullName;
    private String email;
    @JsonProperty(value = "profile_photo")
    private String profilePhoto;
}
