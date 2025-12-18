package com.viddefe.viddefe_api.churches.infrastructure.web;

import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDetailedResDto;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.common.Components.JwtUtil;
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
    public ResponseEntity<ApiResponse<ChurchResDto>> createChurch(@Valid @RequestBody ChurchDTO dto){;
        ChurchResDto response = churchService.addChurch(dto);
        return new ResponseEntity<>(ApiResponse.created(response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChurchDetailedResDto>> getChurchById(@PathVariable UUID id){
        ChurchDetailedResDto church = churchService.getChurchById(id);
        return new ResponseEntity<>(ApiResponse.ok(church), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChurchResDto>> updateChurch(@PathVariable UUID id,
                                                                  @Valid @RequestBody ChurchDTO dto,
                                                                  @CookieValue("access_token") String jwt) {
        UUID updaterPastorId = jwtUtil.getUserId(jwt);
        ChurchResDto response = churchService.updateChurch(id, dto, updaterPastorId);
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChurch(@PathVariable UUID id) {
        churchService.deleteChurch(id);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{churchId}/childrens")
    public ResponseEntity<ApiResponse<Page<ChurchResDto>>> getChildChurches(
            @PathVariable UUID churchId,
            Pageable pageable
    ) {
        Page<ChurchResDto> response = churchService.getChildrenChurches(pageable, churchId);
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }
    @PostMapping("/{churchId}/childrens")
    public ResponseEntity<ApiResponse<ChurchResDto>> addChildChurch(@PathVariable UUID churchId,
                                                                    @Valid @RequestBody ChurchDTO dto,
                                                                    @CookieValue("access_token") String jwt){
        UUID creatorPastorId = jwtUtil.getUserId(jwt);
        ChurchResDto response = churchService.addChildChurch(churchId, dto, creatorPastorId);
        return new ResponseEntity<>(ApiResponse.created(response), HttpStatus.CREATED);
        }
}
