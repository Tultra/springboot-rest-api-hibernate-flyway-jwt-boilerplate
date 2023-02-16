package com.springboot.boilerplate.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="roles")
public class Role {

    @Id
    private Long id;

    @NotNull
    private String name;

    @ManyToMany(mappedBy="roles")
    private Set<Usuario> usuarios = new HashSet<>();

}
