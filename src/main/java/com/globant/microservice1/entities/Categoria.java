package com.globant.microservice1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categorias")
public class Categoria {
    @Id
    private UUID id;
    @Column(unique = true)
    private String nombre;

    @PrePersist
    private void generarId() {
        this.id = UUID.nameUUIDFromBytes(nombre.getBytes());
    }
}