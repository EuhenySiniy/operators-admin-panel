package yevhen.synii.admin_panel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMetricsResponse {
    @JsonProperty(value = "active_time", index = 0)
    private Float activeTime;
    @JsonProperty(value = "quality_assurance", index = 1)
    private Float qualityAssurance;
    @JsonProperty(value = "processing_speed", index = 2)
    private Float processingSpeed;
    @JsonProperty(value = "knowledge_quality", index = 3)
    private Float knowledgeQuality;
    @JsonProperty(value = "total_kpi", index = 4)
    private Float totalKpi;
}
