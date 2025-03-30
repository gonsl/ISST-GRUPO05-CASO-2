package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.repository.ResumenRepository;
import grupo05.es.resumen.service.ResumenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogo")
@RequiredArgsConstructor
public class ResumenController {

    private final ResumenService resumenService;
    private final ResumenRepository resumenRepository;

    @GetMapping("/resumenes")
    public List<Resumen> obtenerTodos() {
        return resumenRepository.findAll();
    }

    @GetMapping("/resumenes/{id}")
    public ResponseEntity<Resumen> obtenerPorId(@PathVariable Integer id) {
        return resumenRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/resumen")
    public ResponseEntity<String> crearResumen(@RequestBody Resumen resumen) {
        if (resumen.getAutor() == null || resumen.getAutor().getId() == null) {
            return ResponseEntity.badRequest().body("El resumen debe tener un autor con ID válido.");
        }

        resumenService.guardarResumen(resumen);
        return ResponseEntity.ok("Resumen creado correctamente");
    }
}
