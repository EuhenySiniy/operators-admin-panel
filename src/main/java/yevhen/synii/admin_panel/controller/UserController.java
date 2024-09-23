package yevhen.synii.admin_panel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yevhen.synii.admin_panel.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping("/user-metrics")
    public ResponseEntity getUserMetricsById(Long id) {
        return userService.getUserMetrics(id);
    }
}
