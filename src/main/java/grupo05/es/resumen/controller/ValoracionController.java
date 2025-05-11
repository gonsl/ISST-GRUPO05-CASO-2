package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Valoracion;
import grupo05.es.resumen.repository.ValoracionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/valoraciones")
public class ValoracionController {

    private final ValoracionRepository valoracionRepository;

    public ValoracionController(ValoracionRepository valoracionRepository) {
        this.valoracionRepository = valoracionRepository;
    }

    @GetMapping
    public Iterable<Valoracion> getAllValoraciones() {
        return valoracionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Valoracion> getValoracion(@PathVariable Long id) {
        return valoracionRepository.findById(id);
    }

    @GetMapping("/resumen/{resumenId}")
    public List<Valoracion> getValoracionesDeResumen(@PathVariable Long resumenId) {
        return valoracionRepository.findByResumenId(resumenId);
    }

    @GetMapping("/usuario/{email}")
    public List<Valoracion> getValoracionesDeUsuario(@PathVariable String email) {
        return valoracionRepository.findByUsuarioEmail(email);
    }

    @PostMapping
    public Valoracion crearValoracion(@RequestBody Valoracion valoracion) {
        return valoracionRepository.save(valoracion);
    }

    @PutMapping("/{id}")
    public Valoracion actualizarValoracion(@PathVariable Long id, @RequestBody Valoracion valoracion) {
        valoracion.setId(id);
        return valoracionRepository.save(valoracion);
    }

    @DeleteMapping("/{id}")
    public void borrarValoracion(@PathVariable Long id) {
        valoracionRepository.deleteById(id);
    }
}
