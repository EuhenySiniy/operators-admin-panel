package yevhen.synii.admin_panel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSupervisorResponse {
    private String operatorName;
    private String supervisorName;
}
