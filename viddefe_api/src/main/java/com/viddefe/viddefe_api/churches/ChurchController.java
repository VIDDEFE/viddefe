package com.viddefe.viddefe_api.churches;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/church")
@AllArgsConstructor
public class ChurchController {
    private ChurchService churchService;

    @PostMapping
    public ChurchModel createChurch(){
        return new ChurchModel();
    }
}
