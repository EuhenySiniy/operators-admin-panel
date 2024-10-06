package yevhen.synii.admin_panel.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import yevhen.synii.admin_panel.dto.UserMetricsResponse;
import yevhen.synii.admin_panel.dto.UserProfileResponse;
import yevhen.synii.admin_panel.dto.UserSupervisorResponse;

public interface UserService {
    ResponseEntity<UserMetricsResponse> getUserMetrics(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<UserProfileResponse> changeProfileInfo(
            String firstName,
            String lastName,
            String email,
            String profilePhoto,
            HttpServletRequest request,
            HttpServletResponse response
    );

    ResponseEntity<UserSupervisorResponse> setSupervisor(Long userId, Long supervisorId, HttpServletRequest servletRequest);
}
