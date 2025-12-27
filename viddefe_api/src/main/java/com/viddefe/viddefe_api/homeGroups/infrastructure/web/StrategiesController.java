package com.viddefe.viddefe_api.homeGroups.infrastructure.web;

import com.viddefe.viddefe_api.common.Components.JwtUtil;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.homeGroups.contracts.StrategyService;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.StrategyDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups/strategies")
@RequiredArgsConstructor
public class StrategiesController {

    private final StrategyService strategyService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<StrategyDto>> create(
            @Valid @RequestBody StrategyDto strategyDto,
            @CookieValue("access_token") String accessToken
    ) {
        UUID churchId = jwtUtil.getChurchId(accessToken);
        StrategyDto created = strategyService.create(strategyDto, churchId);
        return ResponseEntity.ok(ApiResponse.created(created));
    }

    @PutMapping("/{strategyId}")
    public ResponseEntity<ApiResponse<StrategyDto>> update(
            @PathVariable UUID strategyId,
            @Valid @RequestBody StrategyDto strategyDto,
            @CookieValue("access_token") String accessToken
    ) {
        UUID churchId = jwtUtil.getChurchId(accessToken);
        StrategyDto updated = strategyService.update(strategyDto, churchId, strategyId);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StrategyDto>>> findAll() {
        List<StrategyDto> strategies = strategyService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(strategies));
    }

    @DeleteMapping("/{strategyId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID strategyId
    ) {
        strategyService.deleteById(strategyId);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }
}
