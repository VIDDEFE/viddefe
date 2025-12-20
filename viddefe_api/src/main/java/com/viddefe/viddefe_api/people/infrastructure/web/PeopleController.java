package com.viddefe.viddefe_api.people.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.people.config.PeoplePermissions;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.contracts.PeopleService;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PeopleController {
    private final PeopleService peopleService;
    //
    @PreAuthorize(
            "hasAuthority(T(com.viddefe.viddefe_api.people.config.PeoplePermissions)" +
                    ".PEOPLE_ADD_PEOPLE)"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PeopleResDto>> addPeople(
            @RequestBody @Valid PeopleDTO dto
    ) {
        PeopleResDto person = peopleService.createPeople(dto);
        return new ResponseEntity<>(ApiResponse.created(person), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PeopleResDto>>> getPeople(
            Pageable pageable,
            @RequestParam(required = false) UUID rolId
    ){
        Page<PeopleResDto> people = peopleService.getAllPeople(pageable);
        return ResponseEntity.ok(ApiResponse.ok(people));
    }

    @PreAuthorize(
            "hasAuthority(T(com.viddefe.viddefe_api.people.config.PeoplePermissions)" +
                    ".PEOPLE_EDIT_PEOPLE)"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PeopleResDto>> updatePeople(@Valid @RequestBody PeopleDTO dto, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(peopleService.updatePeople(dto,id)));
    }

    @PreAuthorize(
            "hasAuthority(T(com.viddefe.viddefe_api.people.config.PeoplePermissions)" +
                    ".PEOPLE_DELETE_PEOPLE)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePeople(@PathVariable UUID id) {
        peopleService.deletePeople(id);
        return new ResponseEntity<>(ApiResponse.<Void>noContent(), HttpStatus.NO_CONTENT);
    }

    @PreAuthorize(
            "hasAuthority(T(com.viddefe.viddefe_api.people.config.PeoplePermissions)" +
                    ".PEOPLE_VIEW_PEOPLE)"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PeopleResDto>> getPersonById(
            @PathVariable UUID id
    ){
        PeopleResDto people = peopleService.getPeopleById(id);
        return ResponseEntity.ok(ApiResponse.ok(people));
    }
}