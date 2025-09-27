package com.viddefe.viddefe_api.catalogs.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "states")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // ignora lo que no usemos
public class StatesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name; // mapeamos "departament"

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<CitiesModel> cities = new ArrayList<>();

    @JsonProperty("departament")
    public void setDepartament(String departament) {
        this.name = departament;
    }

    @JsonProperty("cities")
    public void setCities(List<String> cityNames) {
        this.cities = cityNames.stream()
                .map(name -> new CitiesModel(name, this))
                .collect(Collectors.toList());
    }
}
