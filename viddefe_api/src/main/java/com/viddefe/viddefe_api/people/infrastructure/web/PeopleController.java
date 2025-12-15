package com.viddefe.viddefe_api.people.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.contracts.PeopleService;
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
        return new ResponseEntity<>(ApiResponse.created(person), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PeopleDTO>>> getPeople(
            Pageable pageable,
            @RequestParam(required = false) UUID rolId
    ){
        Page<PeopleDTO> people = peopleService.getAllPeople(pageable);
        return ResponseEntity.ok(ApiResponse.ok(people));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PeopleDTO>> updatePeople(@Valid @RequestBody PeopleDTO dto, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(peopleService.updatePeople(dto,id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePeople(@PathVariable UUID id) {
        peopleService.deletePeople(id);
        return new ResponseEntity<>(ApiResponse.<Void>noContent(), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PeopleDTO>> getPersonById(
            @PathVariable UUID id
    ){
        PeopleDTO people = peopleService.getPeopleById(id);
        return ResponseEntity.ok(ApiResponse.ok(people));
    }
}