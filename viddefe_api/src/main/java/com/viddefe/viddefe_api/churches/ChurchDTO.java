package com.viddefe.viddefe_api.churches;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class ChurchDTO {
    private UUID id;
    private String name;
    private BigDecimal longitude;
    private BigDecimal stateId;
}
