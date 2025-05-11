package grupo05.es.resumen.repository;

import grupo05.es.resumen.model.Resumen;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResumenRepository extends JpaRepository<Resumen, Long> {
    List<Resumen> findByEscritorEmail(String email);
    List<Resumen> findByGratuitoTrue();

    Optional<Resumen> findByTitulo(String titulo);

    @Query("SELECT r FROM Resumen r WHERE " +
    "LOWER(r.titulo) LIKE LOWER(CONCAT('%', :queryText, '%')) OR " +
            "LOWER(r.autorOriginal) LIKE LOWER(CONCAT('%', :queryText, '%')) OR " +
            "LOWER(r.resumenTexto) LIKE LOWER(CONCAT('%', :queryText, '%')) OR " +
            "LOWER(r.escritorEmail) LIKE LOWER(CONCAT('%', :queryText, '%'))" )
    List<Resumen> findResumenesByQueryText(@Param("queryText") String queryText);

    Optional<List<Resumen>> findByCategoria(String categoria);

    Optional<List<Resumen>> findAllByOrderByValoracionMediaDesc();
}
