package com.viddefe.viddefe_api.people.controller;

import com.viddefe.viddefe_api.people.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.model.PeopleModel;
import com.viddefe.viddefe_api.people.service.PeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PeopleController {
    private final PeopleService peopleService;
    //
    @PostMapping
    public ResponseEntity<PeopleModel> addPeople(@RequestBody @Validated PeopleDTO dto) {
        try {
            PeopleModel person = peopleService.createPeople(dto);
            return ResponseEntity.created(URI.create(person.getId().toString())).body(person);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public ResponseEntity<Page<PeopleModel>> getPeople(Pageable pageable) {
        Page<PeopleModel> people = peopleService.getAllPeople(pageable);
        return ResponseEntity.ok(people);
    }

    @PutMapping("/:id")
    public ResponseEntity<PeopleModel> updatePeople(@RequestBody @Validated PeopleDTO dto, @PathVariable UUID id) {
        try{
            return ResponseEntity.ok(peopleService.updatePeople(dto,id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/:id")
    public ResponseEntity<Void> deletePeople(@PathVariable UUID id) {
        try{
            peopleService.deletePeople(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}