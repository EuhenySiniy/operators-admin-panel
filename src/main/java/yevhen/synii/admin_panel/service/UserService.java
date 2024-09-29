package yevhen.synii.admin_panel.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import yevhen.synii.admin_panel.dto.UserMetricsResponse;
import yevhen.synii.admin_panel.dto.UserProfileResponse;
import yevhen.synii.admin_panel.dto.UserSupervisorResponse;

public interface UserService {
    ResponseEntity<UserMetricsResponse> getUserMetrics(Long id);

    ResponseEntity<UserProfileResponse> changeProfileInfo(
            String firstName,
            String lastName,
            String email,
            String profilePhoto,
            Long id
    );

    ResponseEntity<UserSupervisorResponse> setSupervisor(Long userId, Long supervisorId, HttpServletRequest servletRequest);
}
