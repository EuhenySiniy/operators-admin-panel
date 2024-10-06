package yevhen.synii.admin_panel.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yevhen.synii.admin_panel.dto.UserMetricsResponse;
import yevhen.synii.admin_panel.dto.UserProfileResponse;
import yevhen.synii.admin_panel.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping("/user-metrics")
    public ResponseEntity<UserMetricsResponse> getUserMetrics(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return userService.getUserMetrics(request, response);
    }

    @PutMapping("/change-profile")
    public ResponseEntity<UserProfileResponse> changeProfileInfo(
            @RequestParam(name = "first_name") String firstName,
            @RequestParam(name = "last_name") String lastName,
            @RequestParam String email,
            @RequestParam(name = "profile_photo") String profilePhoto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return userService.changeProfileInfo(firstName, lastName, email, profilePhoto, request, response);
    }

    @PutMapping("/set-supervisor")
    public ResponseEntity<?> setSupervisor(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "supervisor_id") Long supervisorId,
            HttpServletRequest servletRequest) {
        return userService.setSupervisor(userId, supervisorId, servletRequest);
    }
}
