package com.viddefe.viddefe_api.finances.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter @Setter
public class OfferingDtoPageWithAnalityc {
    private Page<OfferingDto> offerings;
    private List<OfferingAnalityc> analitycs;
}
