package com.springboot.boilerplate.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/list-usuarios")
    public List<Usuario> getUsuarios() {
        return usuarioService.findAllUsuarios();
    }

//    @PostMapping("/delete-usuario")
//    @PostMapping("/update-usuario")
//    @PostMapping("/update-roles")
//    @GetMapping("/find-usuarios/{name}")
//    @GetMapping("/count-usuarios")

}
