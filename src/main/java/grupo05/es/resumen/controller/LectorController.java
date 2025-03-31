package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Lector;
import grupo05.es.resumen.model.LectorPrivado;
import grupo05.es.resumen.service.LectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lector")
public class LectorController {
    @Autowired
    private LectorService lectorService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Lector>> getAllLectores(){
        return ResponseEntity.ok(lectorService.getAllLectores());
    }

    @GetMapping("/find/{nombre}")
    public ResponseEntity<Lector> getLectorByNombre(
            @PathVariable String nombre){

        return lectorService.getLectorByNombre(nombre)!=null?
                ResponseEntity.ok(lectorService.getLectorByNombre(nombre)):
                ResponseEntity.noContent().build();
    }

    @PostMapping("/post")
    public ResponseEntity<String> postLector(@RequestBody LectorPrivado lector){

        boolean isPosted = lectorService.postLector(lector);
        return isPosted?
                ResponseEntity.ok("Se ha creado el Lector"):
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{nombre}")
    public ResponseEntity<String> deleteLector(
            @PathVariable String nombre){

        boolean isDeleted = lectorService.deleteLector(nombre);
        return isDeleted?
                ResponseEntity.ok("Se ha eliminado el Lector"):
                ResponseEntity.badRequest().build();
    }

    @PutMapping("/put")
    public ResponseEntity<String> putLector(LectorPrivado lectorPrivado){

        boolean isActualizado = lectorService.putLector(lectorPrivado);
        return isActualizado?
                ResponseEntity.ok("Se ha actualizado el Lector"):
                ResponseEntity.badRequest().build();
    }

}
