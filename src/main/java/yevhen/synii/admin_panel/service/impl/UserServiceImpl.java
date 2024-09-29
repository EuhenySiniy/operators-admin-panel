package yevhen.synii.admin_panel.service.impl;

import jakarta.servlet.http.HttpServletRequest;
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
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.UserService;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepo repo;
    private final JwtServiceImpl jwtServiceImpl;

    @Override
    public ResponseEntity<UserMetricsResponse> getUserMetrics(Long id) {
        var userEntity = repo.findById(id)
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
            Long id
    ) {
        if(firstName.isEmpty() && lastName.isEmpty() && email.isEmpty() && profilePhoto.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        UserEntity user = repo.findById(id)
                .orElseThrow(() -> new UserIsNotFound("User with this id is not exists"));
        if(!firstName.isEmpty()) {
            user.setFirstName(firstName);
        }
        if(!lastName.isEmpty()) {
            user.setLastName(lastName);
        }
        if(!email.isEmpty()) {
            user.setEmail(email);
        }
        if(!profilePhoto.isEmpty()) {
            user.setProfilePhoto(profilePhoto);
        }
        repo.changeUserProfile(
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
        UserEntity userEntity = repo.findByEmail(jwtServiceImpl.extractUsername(jwt))
                .orElseThrow(() -> new UserIsNotFound("User is not found exception"));
        if (userEntity.getRole() != UserRole.SUPERADMIN) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        UserEntity operator = repo.findById(userId)
                .orElseThrow(() -> new UserIsNotFound("User with ID: " + userId + " is not found"));
        UserEntity supervisor = repo.findById(supervisorId)
                .orElseThrow(() -> new UserIsNotFound("User with ID: " + supervisorId + " is not found"));
        if (supervisor.getRole() != UserRole.SUPERVISOR) {
            throw new BadRequestException("User with ID: " + supervisorId + " is not supervisor");
        }
        if (operator.getRole() != UserRole.OPERATOR) {
            throw new BadRequestException("User with ID: " + userId + " is not operator");
        }
        repo.setSupervisorToOperator(userId, supervisorId);
        String operatorName = operator.getFirstName() + " " + operator.getLastName();
        String supervisorName = supervisor.getFirstName() + " " + supervisor.getLastName();
        return new ResponseEntity<>(UserSupervisorResponse.builder()
                .operatorName(operatorName)
                .supervisorName(supervisorName)
                .build(),
                HttpStatus.OK);
    }


}
