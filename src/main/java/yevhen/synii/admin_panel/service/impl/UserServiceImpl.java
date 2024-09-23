package yevhen.synii.admin_panel.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.dto.UserMetricsResponse;
import yevhen.synii.admin_panel.exception.UserIsNotFound;
import yevhen.synii.admin_panel.repository.UsersRepo;
import yevhen.synii.admin_panel.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepo repo;

    @Override
    public ResponseEntity getUserMetrics(Long id) {
        var userEntity = repo.findById(id)
                .orElseThrow(() -> new UserIsNotFound("User with this id is not exists"));
        UserMetricsResponse userMetrics = UserMetricsResponse.builder()
                .activeTime(userEntity.getActiveTime())
                .qualityAssurance(userEntity.getQualityAssurance())
                .processingSpeed(userEntity.getProcessingSpeed())
                .knowledgeQuality(userEntity.getKnowledgeQuality())
                .totalKpi(userEntity.getTotalKpi())
                .build();
        return new ResponseEntity(userMetrics,
                HttpStatus.OK);
    }
}
