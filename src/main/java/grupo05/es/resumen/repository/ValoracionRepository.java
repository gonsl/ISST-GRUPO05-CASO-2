package grupo05.es.resumen.repository;

import grupo05.es.resumen.model.Valoracion;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ValoracionRepository extends CrudRepository<Valoracion, Long> {
    List<Valoracion> findByResumenId(Long resumenId);
    List<Valoracion> findByUsuarioEmail(String email);
}

