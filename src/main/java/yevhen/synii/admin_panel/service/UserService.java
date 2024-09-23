package yevhen.synii.admin_panel.service;

import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity getUserMetrics(Long id);
}
