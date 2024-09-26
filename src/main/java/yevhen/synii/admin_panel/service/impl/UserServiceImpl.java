package yevhen.synii.admin_panel.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.dto.UserMetricsResponse;
import yevhen.synii.admin_panel.dto.UserProfileResponse;
import yevhen.synii.admin_panel.entity.UserEntity;
import yevhen.synii.admin_panel.exception.UserIsNotFound;
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.UserService;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepo repo;

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
        UserProfileResponse userResponse = UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePhoto(user.getProfilePhoto())
                .build();
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
