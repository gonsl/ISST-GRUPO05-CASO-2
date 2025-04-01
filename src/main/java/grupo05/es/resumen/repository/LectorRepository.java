package grupo05.es.resumen.repository;

import grupo05.es.resumen.model.Lector;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectorRepository extends JpaRepository<Lector, Integer> {
    Lector findByNombre(String lectorName);

    Lector findByEmail(String email);
}
