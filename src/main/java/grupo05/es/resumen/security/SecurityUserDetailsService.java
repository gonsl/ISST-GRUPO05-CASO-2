package grupo05.es.resumen.security;

import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public SecurityUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol().toString()) // VISITANTE, LECTOR, etc.
                .build();
    }
}
