package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Autor;
import grupo05.es.resumen.model.AutorPrivado;
import grupo05.es.resumen.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/API/Autor")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @GetMapping("/find/{autorName}")
    public ResponseEntity<Autor> buscaAutor(
            @PathVariable String autorName){

        return autorService.getAutorByName(autorName)==null?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(autorService.getAutorByName(autorName));
    }


    @GetMapping("/findAll")
    public ResponseEntity<List<Autor>> buscaTodosAutores(){

        List<Autor> autorList = autorService.getAllAutores();

        return autorList.isEmpty()?
                ResponseEntity.noContent().build():
                ResponseEntity.ok(autorList);
    }


    @PostMapping("/post")
    public ResponseEntity<String> postAutor(
            @RequestBody AutorPrivado autor){

        Boolean isPosted = autorService.postAutor(autor);

        return isPosted?
                ResponseEntity.ok("Se ha subido el autor"):
                ResponseEntity.badRequest().build();
    }


    @DeleteMapping("/delete/{autorName}")
    public ResponseEntity<String> deleteAutor(
            @PathVariable String autorName){

        Boolean isDeleted = autorService.deleteAutorByNombre(autorName);

        return isDeleted?
                ResponseEntity.ok("Se ha eliminado el autor "+autorName):
                ResponseEntity.badRequest().build();
    }
}
