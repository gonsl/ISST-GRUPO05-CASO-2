package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public Iterable<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{email}")
    public Optional<Usuario> getUsuario(@PathVariable String email) {
        return usuarioRepository.findById(email);
    }

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @PutMapping("/{email}")
    public Usuario actualizarUsuario(@PathVariable String email, @RequestBody Usuario usuario) {
        usuario.setEmail(email);
        return usuarioRepository.save(usuario);
    }

    @DeleteMapping("/{email}")
    public void borrarUsuario(@PathVariable String email) {
        usuarioRepository.deleteById(email);
    }
}
