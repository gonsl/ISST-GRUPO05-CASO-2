package grupo05.es.resumen.repository;

import grupo05.es.resumen.model.Visitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitanteRepository extends JpaRepository<Visitante, Integer> {
    Visitante findByNombre(String nombre);

    Visitante findByEmail(String email);
}
