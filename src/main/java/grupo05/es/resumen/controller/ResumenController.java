package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.model.ResumenPrivado;
import grupo05.es.resumen.repository.ResumenRepository;
import grupo05.es.resumen.service.ResumenService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
    public ResponseEntity<List<Resumen>> getAllResumen(){
        List<Resumen> resumenList= resumenService.getAllResumenes();
        return resumenList.isEmpty()?
                ResponseEntity.notFound().build():
                ResponseEntity.ok(resumenList);
    }


    @GetMapping("/resumenes/{id}")
    public ResponseEntity<Resumen> obtenerPorId(@PathVariable Integer id) {
        Resumen resumen = resumenService.getResumenById(id);
        return resumen==null?
                ResponseEntity.notFound().build():
                ResponseEntity.ok(resumen);
    }
    @GetMapping("/resumen/{nombre}")
    public ResponseEntity<Resumen> obtenerPorTitulo(
            @PathVariable String titulo){
        Resumen resumen = resumenService.getResumenByNombre(titulo);
        return resumen==null?
                ResponseEntity.notFound().build():
                ResponseEntity.ok(resumen);
    }

    @PostMapping("/resumen/post")
    public ResponseEntity<String> crearResumen(@RequestBody ResumenPrivado resumen) {
        Boolean isPosted = resumenService.postResumen(resumen);
        return isPosted?
                ResponseEntity.ok("Resumen creado correctamente"):
                ResponseEntity.badRequest().body("No se ha podido crear el resumen");
    }
    @PutMapping("/resumen/put")
    public ResponseEntity<String> actualizarResumen(@RequestBody ResumenPrivado resumenPrivado){
        Boolean isActualizado = resumenService.putResumen(resumenPrivado);
        return isActualizado?
                ResponseEntity.ok("Resumen actualizado"):
                ResponseEntity.notFound().build();
    }
    @DeleteMapping("/delete/{titulo}")
    public ResponseEntity<String> deleteResumen(@PathVariable String titulo){
        Boolean isDeleted = resumenService.deleteResumen(titulo);
        return isDeleted?
                ResponseEntity.ok("Resumen eliminado"):
                ResponseEntity.notFound().build();
    }
}
