package com.viddefe.viddefe_api.churches.infrastructure.web;

import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;

@RestController
@RequestMapping("/churches")
@RequiredArgsConstructor
public class ChurchController {
    private final ChurchService churchService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChurchModel>> createChurch(@Valid @RequestBody ChurchDTO dto){
        ChurchModel response = churchService.addChurch(dto);
        return new ResponseEntity<>(ApiResponse.created(response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ChurchModel>>> getChurches(Pageable pageable){
        Page<ChurchModel> churches = churchService.getChurches(pageable);
        return ResponseEntity.ok(ApiResponse.ok(churches));
    }
}
