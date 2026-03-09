package com.opex.backend.controller;

import com.opex.backend.dto.UserUpdateRequest;
import com.opex.backend.model.User;
import com.opex.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // Inietta userService in automatico!
public class UserController {

    private final UserService userService;

    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.syncUserWithKeycloak(jwt);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint per aggiornare i dati aggiuntivi.
     * Metodo: PATCH
     * URL: http://localhost:8080/api/users/profile
     * Body (JSON): { "customerId": "123", "dob": "1990-05-20", "question1": "Risposta..." }
     */
    @PatchMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserUpdateRequest request) { // Spring converte il JSON in questo oggetto

        // Estraiamo sempre in modo sicuro l'ID dal token! Mai fidarsi del frontend.
        String keycloakId = jwt.getClaimAsString("sub");

        User updatedUser = userService.updateAdditionalData(keycloakId, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint per cancellare il proprio account da keycloak. Nel nostro DB diventa uetnte disattivato.
     * Metodo: DELETE
     * URL: http://localhost:8080/api/users/profile
     */
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal Jwt jwt) {
        // Estraiamo sempre l'ID in modo sicuro dal Token
        String keycloakId = jwt.getClaimAsString("sub");

        userService.deleteUser(keycloakId);

        // Ritorniamo 204 No Content (Standard REST per indicare che la cancellazione è andata a buon fine)
        return ResponseEntity.noContent().build();
    }
}