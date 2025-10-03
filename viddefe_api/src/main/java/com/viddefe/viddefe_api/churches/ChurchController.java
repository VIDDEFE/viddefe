package com.viddefe.viddefe_api.churches;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/churches")
@RequiredArgsConstructor
public class ChurchController {
    private final ChurchService churchService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChurchModel>> createChurch(@Valid @RequestBody ChurchDTO dto){
        ChurchModel response = churchService.addChurch(dto);
        return new ResponseEntity<>(ApiResponse.success("Church created",response), HttpStatus.CREATED);
    }
}
