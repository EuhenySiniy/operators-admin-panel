package yevhen.synii.admin_panel.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import yevhen.synii.admin_panel.dto.AuthenticationRequest;
import yevhen.synii.admin_panel.dto.AuthenticationResponse;
import yevhen.synii.admin_panel.dto.RegisterRequest;
import yevhen.synii.admin_panel.dto.UserInfoResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response);


    ResponseEntity getUserProfile(HttpServletRequest request, HttpServletResponse response);
}
