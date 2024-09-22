package yevhen.synii.admin_panel.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.dto.AuthenticationRequest;
import yevhen.synii.admin_panel.dto.AuthenticationResponse;
import yevhen.synii.admin_panel.dto.RegisterRequest;
import yevhen.synii.admin_panel.dto.UserInfoResponse;
import yevhen.synii.admin_panel.entity.UserEntity;
import yevhen.synii.admin_panel.entity.enums.UserRole;
import yevhen.synii.admin_panel.entity.enums.UserStatus;
import yevhen.synii.admin_panel.exception.EmailIsAlreadyTaken;
import yevhen.synii.admin_panel.exception.UserHasBeenDeactivated;
import yevhen.synii.admin_panel.exception.UserIsNotFound;
import yevhen.synii.admin_panel.exception.WrongPassword;
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.AuthenticationService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsersRepo repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationResponse register(RegisterRequest request) {
        if(repo.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailIsAlreadyTaken("Email was already taken");
        }
        UserEntity userEntity = UserEntity
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
        var jwtAccessToken = jwtService.generateAccessToken(userEntity);
        var jwtRefreshToken = jwtService.generateRefreshToken(userEntity);
        return AuthenticationResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserIsNotFound("User with this email is not exists"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongPassword("Wrong password");
        }
        if(!isUserActive(user.getStatus())) {
            throw new UserHasBeenDeactivated("User has been deactivated");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        var jwtAccessToken = jwtService.generateAccessToken(user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        if(jwtService.isTokenValid(jwt, userDetails)) {
            var jwtAccessToken = jwtService.generateAccessToken(userDetails);
            var jwtRefreshToken = jwtService.generateRefreshToken(userDetails);

            return new ResponseEntity(new AuthenticationResponse(jwtAccessToken, jwtRefreshToken), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity getUserProfile(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String fullName;
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        var userEntity = repo.findByEmail(userEmail)
                .orElseThrow(() -> new UserIsNotFound("User with this email is not exists"));
        fullName = userEntity.getFirstName() + " " + userEntity.getLastName();
        UserInfoResponse userResponse = UserInfoResponse.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .fullName(fullName)
                .profilePhoto(userEntity.getProfilePhoto())
                .startWorkAt(userEntity.getStartedWork())
                .nextShiftAt(userEntity.getNextShift())
                .build();
        return new ResponseEntity(userResponse,
                HttpStatus.OK);
    }

    public boolean isUserActive(UserStatus status) {
        return status.equals(UserStatus.ACTIVE);
    }
}
