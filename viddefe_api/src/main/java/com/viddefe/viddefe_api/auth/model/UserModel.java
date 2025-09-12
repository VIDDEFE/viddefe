package com.viddefe.viddefe_api.auth.model;

import com.viddefe.viddefe_api.people.model.PeopleModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserModel {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "people_id")
    private PeopleModel people;

    private String email;

    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_user_id")
    private RolUserModel rolUser;

}
