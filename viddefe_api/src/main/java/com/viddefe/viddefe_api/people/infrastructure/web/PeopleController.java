package com.viddefe.viddefe_api.people.infrastructure.web;

import com.viddefe.viddefe_api.common.Components.JwtUtil;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.contracts.PeopleService;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PeopleController {
    private final PeopleService peopleService;
    private final JwtUtil jwtUtil;
    //
    @PreAuthorize(
            "hasAuthority(T(com.viddefe.viddefe_api.people.config.PeoplePermissions)" +
                    ".PEOPLE_ADD_PEOPLE)"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PeopleResDto>> addPeople(
            @RequestBody @Valid PeopleDTO dto,
            @CookieValue(value = "access_token") String jwtToken
    ) {

        UUID churchIdFromJwt = jwtUtil.getChurchId(jwtToken);
        UUID churchIdFromDto = dto.getChurchId() == null ? churchIdFromJwt : dto.getChurchId();
        dto.setChurchId(churchIdFromDto);
        PeopleResDto person = peopleService.createPeople(dto);
        return new ResponseEntity<>(ApiResponse.created(person), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PeopleResDto>>> getPeople(
            Pageable pageable,
            @RequestParam(required = false) Long typePersonId,
            @RequestParam(required = false)AttendanceQualityEnum attendanceQuality,
            @CookieValue(value = "access_token") String jwtToken
    ){
        UUID churchId = jwtUtil.getChurchId(jwtToken);
        Page<PeopleResDto> people = peopleService.getAllPeople(pageable, typePersonId, churchId, attendanceQuality);
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