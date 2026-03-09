package com.opex.backend.service;

import com.opex.backend.model.User;
import com.opex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Inietta il client Keycloak che abbiamo configurato prima
    private final Keycloak keycloak;

    // Leggiamo il nome del realm in cui lavoriamo ("opex")
    @Value("${keycloak-admin.target-realm}")
    private String targetRealm;

    @Transactional
    public User syncUserWithKeycloak(Jwt jwt) {
        String keycloakId = jwt.getClaimAsString("sub");
        Optional<User> existingUser = userRepository.findById(keycloakId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        User newUser = new User(keycloakId, email, firstName, lastName);
        System.out.println("Nuovo utente sincronizzato nel DB: " + email);

        return userRepository.save(newUser);
    }

    @Transactional
    public User updateAdditionalData(String keycloakId, com.opex.backend.dto.UserUpdateRequest request) {
        // 1. Recupero l'utente dal mio Database locale
        User user = userRepository.findById(keycloakId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato nel DB locale"));

        boolean updateKeycloak = false;

        // 2. Controllo i campi base: Se li modifico, aggiorno anche il DB locale
        // e segno che devo fare la chiamata alle API di Keycloak
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
            updateKeycloak = true;
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
            updateKeycloak = true;
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
            updateKeycloak = true;
        }

        // 3. SE SERVE: Faccio la chiamata a Keycloak per allineare il suo DB!
        if (updateKeycloak) {
            // Prendo l'utente specifico da Keycloak
            UserResource userResource = keycloak.realm(targetRealm).users().get(keycloakId);

            // Scarico i suoi dati attuali (Best Practice per non cancellare altri campi)
            UserRepresentation kcUser = userResource.toRepresentation();

            // Imposto i nuovi valori (se sono presenti)
            if (request.getEmail() != null) kcUser.setEmail(request.getEmail());
            if (request.getFirstName() != null) kcUser.setFirstName(request.getFirstName());
            if (request.getLastName() != null) kcUser.setLastName(request.getLastName());

            // Mando il comando di UPDATE a Keycloak!
            userResource.update(kcUser);
            System.out.println("Aggiornato utente " + keycloakId + " anche su Keycloak!");
        }

        // 4. Aggiorno i campi extra (esclusivi del DB PostgreSQL)
        if (request.getCustomerId() != null) user.setCustomerId(request.getCustomerId());
        if (request.getConnectionId() != null) user.setConnectionId(request.getConnectionId());
        if (request.getDob() != null) user.setDob(request.getDob());
        if (request.getAnswer1() != null) user.setAnswer1(request.getAnswer1());
        if (request.getAnswer2() != null) user.setAnswer2(request.getAnswer2());
        if (request.getAnswer3() != null) user.setAnswer3(request.getAnswer3());
        if (request.getAnswer4() != null) user.setAnswer4(request.getAnswer4());
        if (request.getAnswer5() != null) user.setAnswer5(request.getAnswer5());

        // 5. Salvo tutto sul mio Database PostgreSQL
        return userRepository.save(user);
    }

    // --- METODO PER ELIMINARE L'UTENTE (SOFT DELETE DB + HARD DELETE KEYCLOAK) ---
    @Transactional
    public void deleteUser(String keycloakId) {
        // 1. Cerco l'utente nel mio DB locale
        User user = userRepository.findById(keycloakId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato nel DB locale"));

        // 2. SOFT DELETE: Non lo cancello fisicamente, ma lo disattivo
        user.setIsActive(false);
        userRepository.save(user); // Salvo la modifica nel DB locale

        // 3. HARD DELETE SU KEYCLOAK: Lo elimino definitivamente dall'Identity Provider
        try {
            keycloak.realm(targetRealm).users().get(keycloakId).remove();
            System.out.println("Utente " + keycloakId + " eliminato da Keycloak e disattivato nel DB locale.");
        } catch (Exception e) {
            // Se per qualche motivo Keycloak fallisce (es. utente già rimosso a mano),
            // lanciamo un'eccezione così Spring fa il "Rollback" e rimette isActive=true nel DB!
            throw new RuntimeException("Errore durante la cancellazione su Keycloak: " + e.getMessage());
        }
    }
}