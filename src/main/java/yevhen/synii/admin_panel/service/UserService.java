package yevhen.synii.admin_panel.service;

import org.springframework.http.ResponseEntity;
import yevhen.synii.admin_panel.dto.UserMetricsResponse;
import yevhen.synii.admin_panel.dto.UserProfileResponse;

public interface UserService {
    ResponseEntity<UserMetricsResponse> getUserMetrics(Long id);

    ResponseEntity<UserProfileResponse> changeProfileInfo(
            String firstName,
            String lastName,
            String email,
            String profilePhoto,
            Long id
    );
}
