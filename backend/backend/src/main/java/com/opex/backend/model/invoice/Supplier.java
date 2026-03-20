package com.opex.backend.model.invoice;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier")
@Getter
@Setter
@NoArgsConstructor
public class Supplier {

    @Id
    private String supplierId;
    private String name;
    private String contact;
    private String email;
    private String phone;
    private String taxId;
    private String status;
    private String spend;
}
