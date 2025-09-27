package com.viddefe.viddefe_api.people.controller;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.people.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.model.PeopleModel;
import com.viddefe.viddefe_api.people.service.PeopleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
@Validated
public class PeopleController {
    private final PeopleService peopleService;
    //
    @PostMapping
    public ResponseEntity<ApiResponse<PeopleModel>> addPeople(
            @RequestBody @Valid PeopleDTO dto
    ) {
        PeopleModel person = peopleService.createPeople(dto);
        return new ResponseEntity<>(ApiResponse.success("People created",person), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PeopleModel>>> getPeople(Pageable pageable) {
        Page<PeopleModel> people = peopleService.getAllPeople(pageable);
        return ResponseEntity.ok(ApiResponse.success("People list",people));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PeopleModel>> updatePeople(@Valid @RequestBody PeopleDTO dto, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Person with id "+ id.toString() + "was updated",peopleService.updatePeople(dto,id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePeople(@PathVariable UUID id) {
        peopleService.deletePeople(id);
        return ResponseEntity.ok(ApiResponse.success("Person with id "+ id.toString() + "was deleted"));
    }
}