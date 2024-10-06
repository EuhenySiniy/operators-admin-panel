package yevhen.synii.admin_panel.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.dto.AuthenticationRequest;
import yevhen.synii.admin_panel.dto.AuthenticationResponse;
import yevhen.synii.admin_panel.dto.RegisterRequest;
import yevhen.synii.admin_panel.dto.UserInfoResponse;
import yevhen.synii.admin_panel.entity.TokenEntity;
import yevhen.synii.admin_panel.entity.UserEntity;
import yevhen.synii.admin_panel.entity.enums.UserRole;
import yevhen.synii.admin_panel.entity.enums.UserStatus;
import yevhen.synii.admin_panel.exception.EmailIsAlreadyTaken;
import yevhen.synii.admin_panel.exception.UserHasBeenDeactivated;
import yevhen.synii.admin_panel.exception.UserIsNotFound;
import yevhen.synii.admin_panel.exception.WrongPassword;
import yevhen.synii.admin_panel.repository.TokensRepo;
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.AuthenticationService;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final TokensRepo tokensRepo;
    private final UsersRepo repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtServiceImpl;
    private final AuthenticationManager authenticationManager;

    @Override
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
        UserEntity savedUser = repo.save(userEntity);
        String jwtAccessToken = jwtServiceImpl.generateAccessToken(userEntity, savedUser.getId(), savedUser.getRole());
        String jwtRefreshToken = jwtServiceImpl.generateRefreshToken(userEntity);
        tokensRepo.save(TokenEntity.builder()
                        .userEntity(savedUser)
                        .accessToken(jwtAccessToken)
                        .refreshToken(jwtRefreshToken)
                        .expired(false)
                        .accessExpiresAt(jwtServiceImpl.tokenExpiration(jwtAccessToken))
                        .refreshExpiresAt(jwtServiceImpl.tokenExpiration(jwtRefreshToken))
                        .created_at(Timestamp.valueOf(LocalDateTime.now()))
                        .updated_at(Timestamp.valueOf(LocalDateTime.now()))
                        .build());
        return AuthenticationResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String jwtAccessToken;
        String jwtRefreshToken;
        UserEntity user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserIsNotFound("User with this email is not exists"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongPassword("Wrong password");
        }
        if(!isUserActive(user.getStatus())) {
            throw new UserHasBeenDeactivated("User has been deactivated");
        }
        TokenEntity tokenEntity = tokensRepo.getTokenEntity(user.getId());
        if(tokenEntity != null) {
            jwtAccessToken = tokenEntity.getAccessToken();
            jwtRefreshToken = tokenEntity.getRefreshToken();
            return AuthenticationResponse.builder()
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .build();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        jwtAccessToken = jwtServiceImpl.generateAccessToken(user, user.getId(), user.getRole());
        jwtRefreshToken = jwtServiceImpl.generateRefreshToken(user);
        tokensRepo.save(TokenEntity.builder()
                .userEntity(user)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .expired(false)
                .accessExpiresAt(jwtServiceImpl.tokenExpiration(jwtAccessToken))
                .refreshExpiresAt(jwtServiceImpl.tokenExpiration(jwtRefreshToken))
                .created_at(Timestamp.valueOf(LocalDateTime.now()))
                .updated_at(Timestamp.valueOf(LocalDateTime.now()))
                .build());
        return AuthenticationResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    @Override
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        jwt = authHeader.substring(7);
        userEmail = jwtServiceImpl.extractUsername(jwt);
        UserEntity user = repo.findByEmail(userEmail)
                .orElseThrow(() -> new UserIsNotFound("User with this email is not exists"));
        TokenEntity token = tokensRepo.getTokenEntityByRefreshToken(jwt);
        if(jwtServiceImpl.isTokenValid(jwt, user) && !token.isExpired()) {
            spoilToken(user.getId());
            String jwtAccessToken = jwtServiceImpl.generateAccessToken(user, user.getId(), user.getRole());
            String jwtRefreshToken = jwtServiceImpl.generateRefreshToken(user);
            tokensRepo.save(TokenEntity.builder()
                    .userEntity(user)
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .expired(false)
                    .accessExpiresAt(jwtServiceImpl.tokenExpiration(jwtAccessToken))
                    .refreshExpiresAt(jwtServiceImpl.tokenExpiration(jwtRefreshToken))
                    .created_at(Timestamp.valueOf(LocalDateTime.now()))
                    .updated_at(Timestamp.valueOf(LocalDateTime.now()))
                    .build());
            return new ResponseEntity<>(new AuthenticationResponse(jwtAccessToken, jwtRefreshToken), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> getUserProfile(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String fullName;
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        jwt = authHeader.substring(7);
        Long id = jwtServiceImpl.extractId(jwt);
        var userEntity = repo.findById(id)
                .orElseThrow(() -> new UserIsNotFound("User with this email is not exists"));
        if(!jwtServiceImpl.isTokenValid(jwt, userEntity)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        fullName = userEntity.getFirstName() + " " + userEntity.getLastName();
        String supervisor = "";
        if(userEntity.getSupervisorId() != null) {
            supervisor = userEntity.getSupervisorId().getFirstName() + " " + userEntity.getSupervisorId().getLastName();
        }
        UserInfoResponse userResponse = UserInfoResponse.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .fullName(fullName)
                .profilePhoto(userEntity.getProfilePhoto())
                .startWorkAt(userEntity.getStartedWork())
                .nextShiftAt(userEntity.getNextShift())
                .supervisor(supervisor)
                .build();
        return new ResponseEntity<>(userResponse,
                HttpStatus.OK);
    }

    @Scheduled(cron = "${delete.spoiled.tokens.delay}", zone = "Europe/Paris")
    private void deleteSpoiledTokens() {
        tokensRepo.deleteAllExpiredTokens();
    }

    private boolean isUserActive(UserStatus status) {
        return status.equals(UserStatus.ACTIVE);
    }

    private void spoilToken(Long userId) {
        tokensRepo.killToken(userId);
    }
}
