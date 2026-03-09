package com.opex.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserUpdateRequest {
    // --- Campi base (che andranno anche su Keycloak) ---
    private String email;
    private String firstName;
    private String lastName;

    // --- Campi extra (solo su DB Postgres) ---
    private String customerId;
    private String connectionId;
    private LocalDate dob;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;
}