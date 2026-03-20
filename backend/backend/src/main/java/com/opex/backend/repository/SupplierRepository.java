package com.opex.backend.repository;

import com.opex.backend.model.invoice.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, String> {
    // Nota: Supplier non ha userId nel modello corrente, ma solitamente i fornitori sono legati all'utente/azienda.
    // Se necessario, aggiungerò il supporto per userId in seguito se il modello verrà aggiornato.
}
