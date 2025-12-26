package com.viddefe.viddefe_api.worship.infrastructure.web;

import com.viddefe.viddefe_api.worship.contracts.WorshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetings/worships")
@RequiredArgsConstructor
public class WorshipMeetingsController {
    private final WorshipService worshipService;
}
