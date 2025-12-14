package com.viddefe.viddefe_api.churches.infrastructure.web;

import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.config.Components.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;

import java.util.UUID;

@RestController
@RequestMapping("/churches")
@RequiredArgsConstructor
public class ChurchController {
    private final ChurchService churchService;
    private final JwtUtil jwtUtil;
    @PostMapping
    public ResponseEntity<ApiResponse<ChurchResDto>> createChurch(@Valid @RequestBody ChurchDTO dto,
                                                                  @CookieValue("access_token") String jwt){
        Claims claims = jwtUtil.getClaims(jwt);
        String strCreatorPastorId = claims.getSubject();
        UUID creatorPastorId = UUID.fromString(strCreatorPastorId);
        ChurchResDto response = churchService.addChurch(dto, creatorPastorId);
        return new ResponseEntity<>(ApiResponse.created(response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ChurchResDto>>> getChurches(Pageable pageable){
        Page<ChurchResDto> churches = churchService.getChurches(pageable);
        return ResponseEntity.ok(ApiResponse.ok(churches));
    }
}
