package com.viddefe.viddefe_api.auth.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter @Setter
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "people_id", nullable = false)
    private PeopleModel people;

    private String phone;
    private String email;
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private RolUserModel rolUser;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userModel", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<UserPermissions> permissions;

        public void addListPermission(List<UserPermissions> permission) {
            if(this.permissions == null) {
                this.permissions = permission;
                return;
            }
            this.permissions.addAll(permission);
        }

}
