package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Visitante;
import grupo05.es.resumen.model.VisitantePrivado;
import grupo05.es.resumen.service.VisitanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Visitante")
public class VisitanteController {
    @Autowired
    private VisitanteService visitanteService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Visitante>> getAllVisitantes(){
        List<Visitante> visitanteList = visitanteService.getAllVisitante();
        return visitanteList.isEmpty()?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(visitanteList);
    }

    @GetMapping("/find/{nombre}")
    public ResponseEntity<Visitante> getVisitanteByNombre(
            @PathVariable String nombre){
        Visitante visitante = visitanteService.getVisitanteByNombre(nombre);
        return visitante==null?
                ResponseEntity.notFound().build():
                ResponseEntity.ok(visitante);
    }

    @PostMapping("/post")
    public ResponseEntity<String> postVisitante(
            @RequestBody VisitantePrivado visitantePrivado){
        Boolean isPosted = visitanteService.postVisitante(visitantePrivado);
        return isPosted?
                ResponseEntity.ok("Se ha creado el nuevo visitante"):
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{nombre}")
    public ResponseEntity<String> deleteVisitante(
            @PathVariable String nombre){

        Boolean isDeleted = visitanteService.deleteVisitante(nombre);
        return isDeleted?
                ResponseEntity.ok("Se ha eliminado el visitante"):
                ResponseEntity.notFound().build();
    }

    @PutMapping("/put")
    public ResponseEntity<String> putVisitante(
            @RequestBody VisitantePrivado visitantePrivado){
        Boolean isActualizado = visitanteService.putVisitante(visitantePrivado);
        return isActualizado?
                ResponseEntity.ok("Visitante actualizado"):
                ResponseEntity.notFound().build();
    }
}
