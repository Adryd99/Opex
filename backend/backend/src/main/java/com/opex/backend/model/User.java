package com.opex.backend.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    private String id; // L'ID di Keycloak (es. 5f4e3b2a-...)

    private String email;
    private String firstName;
    private String lastName;

    private String customerId;
    private String identifier;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime blockedAt;
    private java.time.LocalDateTime updatedAt;
    private String connectionId;
    private LocalDate dob;

    @Column(columnDefinition = "TEXT")
    private String answer1;
    @Column(columnDefinition = "TEXT")
    private String answer2;
    @Column(columnDefinition = "TEXT")
    private String answer3;
    @Column(columnDefinition = "TEXT")
    private String answer4;
    @Column(columnDefinition = "TEXT")
    private String answer5;

    private Boolean isActiveSaltedge = false;
    private Boolean isActive = true;

    public User(String id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true; // Quando si registra è ovviamente attivo
    }
}