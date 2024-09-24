package yevhen.synii.admin_panel.service;

import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity getUserMetrics(Long id);

    ResponseEntity changeProfileInfo(
            String firstName,
            String lastName,
            String email,
            String profilePhoto,
            Long id
    );
}
