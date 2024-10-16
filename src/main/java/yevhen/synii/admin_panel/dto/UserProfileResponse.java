package yevhen.synii.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String profilePhoto;
    private String supervisor;
}
