package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.repository.ResumenRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resumenes")
public class ResumenController {

    private final ResumenRepository resumenRepository;

    public ResumenController(ResumenRepository resumenRepository) {
        this.resumenRepository = resumenRepository;
    }

    @GetMapping
    public Iterable<Resumen> getAllResumenes() {
        return resumenRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Resumen> getResumen(@PathVariable Long id) {
        return resumenRepository.findById(id);
    }

    @GetMapping("/escritor/{email}")
    public List<Resumen> getResumenesPorEscritor(@PathVariable String email) {
        return resumenRepository.findByEscritorEmail(email);
    }

    @PostMapping
    public Resumen crearResumen(@RequestBody Resumen resumen) {
        return resumenRepository.save(resumen);
    }

    @PutMapping("/{id}")
    public Resumen actualizarResumen(@PathVariable Long id, @RequestBody Resumen resumen) {
        resumen.setId(id);
        return resumenRepository.save(resumen);
    }

    @DeleteMapping("/{id}")
    public void borrarResumen(@PathVariable Long id) {
        resumenRepository.deleteById(id);
    }
}
