package grupo05.es.resumen.repository;

import grupo05.es.resumen.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor,Integer> {
    Autor findByNombre(String nombre);

    List<Autor> findAllByNombre(String autorName);
}
