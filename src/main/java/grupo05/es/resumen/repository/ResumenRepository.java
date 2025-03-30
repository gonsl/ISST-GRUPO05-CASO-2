package grupo05.es.resumen.repository;

import grupo05.es.resumen.model.Resumen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumenRepository extends JpaRepository<Resumen, Integer> {
}
