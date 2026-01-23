package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance_qualities")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AttendanceQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private AttendanceQualityEnum attendanceQuality;
}
