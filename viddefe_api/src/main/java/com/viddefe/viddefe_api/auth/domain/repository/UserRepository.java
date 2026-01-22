package com.viddefe.viddefe_api.auth.domain.repository;

import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByPhone(String phone);
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
           "FROM UserModel u " +
           "WHERE u.people.id = :personId AND u.people.church.id = :churchId")
    boolean existsUserByPeopleIdAndPeopleChurchId(UUID personId, UUID churchId);

    /**
     * Busca usuario por ID con people y church pre-cargados.
     * Evita N+1 en AuthMeUseCase.getUserInfo()
     */
    @Query("SELECT u FROM UserModel u " +
           "JOIN FETCH u.people p " +
           "LEFT JOIN FETCH p.church c " +
           "LEFT JOIN FETCH c.city ci " +
           "LEFT JOIN FETCH ci.states " +
           "LEFT JOIN FETCH p.state ps " +
           "WHERE u.id = :userId")
    Optional<UserModel> findByIdWithPeopleAndChurch(@Param("userId") UUID userId);

    /**
     * Busca usuario por email con todas las relaciones necesarias para signIn.
     * Evita N+1 en AuthServiceImpl.signIn()
     */
    @Query("SELECT u FROM UserModel u " +
           "JOIN FETCH u.people p " +
           "LEFT JOIN FETCH p.church c " +
           "JOIN FETCH u.rolUser " +
           "LEFT JOIN FETCH p.state ps " +
           "WHERE u.email = :email")
    Optional<UserModel> findByEmailWithRelations(@Param("email") String email);

    /**
     * Busca usuario por teléfono con todas las relaciones necesarias para signIn.
     * Evita N+1 en AuthServiceImpl.signIn()
     */
    @Query("SELECT u FROM UserModel u " +
           "JOIN FETCH u.people p " +
           "LEFT JOIN FETCH p.church c " +
           "JOIN FETCH u.rolUser " +
           "LEFT JOIN FETCH p.state ps " +
           "WHERE u.phone = :phone")
    Optional<UserModel> findByPhoneWithRelations(@Param("phone") String phone);

    Optional<UserModel> findByPeopleId(UUID personId);
}
