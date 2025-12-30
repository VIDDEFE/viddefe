package com.viddefe.viddefe_api.people.infrastructure.web;

import com.viddefe.viddefe_api.people.application.PeopleTypeService;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/people/types")
@RequiredArgsConstructor
public class PeopleTypeController {
    private final PeopleTypeService peopleTypeService;

    @GetMapping
    public ResponseEntity<List<PeopleTypeDto>> getAllPeopleTypes() {
        List<PeopleTypeDto> peopleTypes = peopleTypeService.getAllPeopleTypes();
        return ResponseEntity.ok(peopleTypes);
    }
}
