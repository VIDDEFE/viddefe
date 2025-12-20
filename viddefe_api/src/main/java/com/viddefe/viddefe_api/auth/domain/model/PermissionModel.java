package com.viddefe.viddefe_api.auth.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "permissions")
@Entity
@Getter @Setter
public class PermissionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
}
