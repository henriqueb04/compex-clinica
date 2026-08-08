package com.compex.grupo5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
// Rascunho
public class Medico {
    @Id
    @GeneratedValue
    private Long id;
}