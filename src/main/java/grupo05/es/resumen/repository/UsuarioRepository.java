package grupo05.es.resumen.repository;

import grupo05.es.resumen.model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);

    
}
