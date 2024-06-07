package com.globant.microservice1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nombre", "marca", "fecha"})
})
public class Producto {
    @Id
    private UUID id;

    private String nombre;
    private String marca;
    private Double precio;
    private LocalDate fecha;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @PrePersist
    private void generarId() {
        this.id = UUID.nameUUIDFromBytes((nombre + marca + fecha.toString()).getBytes());
    }
}