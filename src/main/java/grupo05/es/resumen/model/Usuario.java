package grupo05.es.resumen.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Column;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    @Column(nullable = false, unique = true)
    private String email;

    private String nombre;

    @Column(nullable = false)
    private String rol; // VISITANTE, LECTOR, ESCRITOR, ADMIN

    private boolean suscrito; // Solo true si es LECTOR y paga

    private String password; // si quieres añadir login real en el futuro

    @ManyToMany
    @JoinTable(name = "favoritos", joinColumns = @JoinColumn(name = "usuario_email"),
    inverseJoinColumns = @JoinColumn(name = "resumen_id"))
    
    private Set<Resumen> resumenesFavoritos;

    @Column(name = "ha_pagado")
    private boolean haPagado = false;
}

