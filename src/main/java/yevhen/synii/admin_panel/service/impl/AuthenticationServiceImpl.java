package yevhen.synii.admin_panel.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.dto.AuthenticationRequest;
import yevhen.synii.admin_panel.dto.AuthenticationResponse;
import yevhen.synii.admin_panel.dto.RegisterRequest;
import yevhen.synii.admin_panel.entity.UserEntity;
import yevhen.synii.admin_panel.entity.enums.UserRole;
import yevhen.synii.admin_panel.entity.enums.UserStatus;
import yevhen.synii.admin_panel.exception.EmailIsAlreadyTaken;
import yevhen.synii.admin_panel.exception.UserIsNotFound;
import yevhen.synii.admin_panel.exception.WrongPassword;
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.AuthenticationService;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsersRepo repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if(repo.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailIsAlreadyTaken("Email was already taken");
        }
        UserEntity userEntity = (UserEntity) UserEntity
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(UserRole.OPERATOR)
                .status(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(request.getPassword()))
                .created_at(Timestamp.valueOf(LocalDateTime.now()))
                .updated_at(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        repo.save(userEntity);
        var jwtToken = jwtService.generateToken(userEntity);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserIsNotFound("User with this email is not exists"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongPassword("Wrong password");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
