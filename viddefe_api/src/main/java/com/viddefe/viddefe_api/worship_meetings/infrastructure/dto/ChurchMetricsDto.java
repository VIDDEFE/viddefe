package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @Setter
@SuperBuilder
@AllArgsConstructor @NoArgsConstructor
public class ChurchMetricsDto extends  MetricsAttendanceDto {
    private Integer totalGroups;
    private List<MetricsAttendanceDto> groupMetrics, churchMetrics;
}
