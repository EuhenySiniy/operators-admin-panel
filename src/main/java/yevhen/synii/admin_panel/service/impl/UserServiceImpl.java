package yevhen.synii.admin_panel.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.dto.UserMetricsResponse;
import yevhen.synii.admin_panel.dto.UserProfileResponse;
import yevhen.synii.admin_panel.dto.UserSupervisorResponse;
import yevhen.synii.admin_panel.entity.UserEntity;
import yevhen.synii.admin_panel.entity.enums.UserRole;
import yevhen.synii.admin_panel.exception.BadRequestException;
import yevhen.synii.admin_panel.exception.UserIsNotFound;
import yevhen.synii.admin_panel.repository.TokensRepo;
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.UserService;

import java.sql.Timestamp;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepo usersRepo;
    private final TokensRepo tokensRepo;
    private final JwtServiceImpl jwtServiceImpl;

    @Override
    public ResponseEntity<UserMetricsResponse> getUserMetrics(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        jwt = authHeader.substring(7);
        Long id = jwtServiceImpl.extractId(jwt);
        var userEntity = usersRepo.findById(id)
                .orElseThrow(() -> new UserIsNotFound("User with this id is not exists"));
        UserMetricsResponse userMetrics = UserMetricsResponse.builder()
                .activeTime(userEntity.getActiveTime())
                .qualityAssurance(userEntity.getQualityAssurance())
                .processingSpeed(userEntity.getProcessingSpeed())
                .knowledgeQuality(userEntity.getKnowledgeQuality())
                .totalKpi(userEntity.getTotalKpi())
                .build();
        return new ResponseEntity<>(userMetrics,
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserProfileResponse> changeProfileInfo(
            String firstName,
            String lastName,
            String email,
            String profilePhoto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        jwt = authHeader.substring(7);
        Long id = jwtServiceImpl.extractId(jwt);
        if(firstName.isEmpty() && lastName.isEmpty() && email.isEmpty() && profilePhoto.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        UserEntity user = usersRepo.findById(id)
                .orElseThrow(() -> new UserIsNotFound("User with this id is not exists"));
        if(!firstName.isEmpty()) {
            user.setFirstName(firstName);
        }
        if(!lastName.isEmpty()) {
            user.setLastName(lastName);
        }
        if(!email.isEmpty()) {
            user.setEmail(email);
            tokensRepo.killToken(user.getId());
        }
        if(!profilePhoto.isEmpty()) {
            user.setProfilePhoto(profilePhoto);
        }
        usersRepo.changeUserProfile(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfilePhoto(),
                new Timestamp(System.currentTimeMillis()),
                id
        );
        String supervisor = "";
        if(user.getSupervisorId() != null) {
            supervisor = user.getSupervisorId().getFirstName() + " " + user.getSupervisorId().getLastName();
        }
        UserProfileResponse userResponse = UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePhoto(user.getProfilePhoto())
                .supervisor(supervisor)
                .build();
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserSupervisorResponse> setSupervisor(Long userId, Long supervisorId, HttpServletRequest servletRequest) {
        final String authHeader = servletRequest.getHeader("Authorization");
        final String jwt;
        jwt = authHeader.substring(7);
        UserRole role = UserRole.valueOf(jwtServiceImpl.extractRole(jwt));
        if (!role.equals(UserRole.SUPERADMIN)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        UserEntity operator = usersRepo.findById(userId)
                .orElseThrow(() -> new UserIsNotFound("User with ID: " + userId + " is not found"));
        UserEntity supervisor = usersRepo.findById(supervisorId)
                .orElseThrow(() -> new UserIsNotFound("User with ID: " + supervisorId + " is not found"));
        if (operator.getSupervisorId() != null) {
            if (Objects.equals(operator.getSupervisorId().getId(), supervisor.getId())) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        if (supervisor.getRole() != UserRole.SUPERVISOR) {
            throw new BadRequestException("User with ID: " + supervisorId + " is not supervisor");
        }
        if (operator.getRole() != UserRole.OPERATOR) {
            throw new BadRequestException("User with ID: " + userId + " is not operator");
        }
        usersRepo.setSupervisorToOperator(userId, supervisorId);
        String operatorName = operator.getFirstName() + " " + operator.getLastName();
        String supervisorName = supervisor.getFirstName() + " " + supervisor.getLastName();
        return new ResponseEntity<>(UserSupervisorResponse.builder()
                .operatorName(operatorName)
                .supervisorName(supervisorName)
                .build(),
                HttpStatus.OK);
    }


}
