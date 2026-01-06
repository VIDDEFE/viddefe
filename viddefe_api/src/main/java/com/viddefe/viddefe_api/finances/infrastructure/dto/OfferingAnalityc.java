package com.viddefe.viddefe_api.finances.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Setter
@Getter
public class OfferingAnalityc {
    private String code, name; // OFFERING , OFRENDA_ESPECIAL, DIEZMO
    private Double amount;
    private Long count;

}
