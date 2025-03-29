package grupo05.es.resumen.service;

import grupo05.es.resumen.model.Autor;
import grupo05.es.resumen.repository.AutorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public Autor getAutorByName(String autorName){
        Autor autorEncontrado= autorRepository.findByNombre(autorName);
        if(autorEncontrado== null){
            log.warn("No existe el autor");
            return null;
        }
        return autorEncontrado;
    }

    public List<Autor> getAllAutores(){
        List<Autor> autorList = autorRepository.findAll();
        if(!autorList.isEmpty()){
            log.info("Devolviendo odos los Autores");
            return autorList;
        }
        log.warn("No existen Autores En la BD");
        return  autorList;
    }

/* -------CREO QUE NO HACE FALTA--------------
    public List<Autor> getAllAutorByName(String autorName){
        List<Autor>  autorList = autorRepository.findAllByNombre(autorName);

        if(!autorList.isEmpty()){
            return autorList;
        }

        log.warn("No hay autores con ese nombre");
        return null;
    }*/

    public Boolean postAutor(Autor autor) {
        if (getAutorByName(autor.getNombre()) == null) {
            autorRepository.save(autor);
            log.info("Se ha creado el autor");
            return true;
        } else {
            log.warn("Ya existe un autor con ese nombre");
            return false;
        }
    }

    public Boolean deleteAutorByNombre(String autorName) {
        Autor autorEncontrado = getAutorByName(autorName);
        if (autorEncontrado == null) {
            log.warn("No existe un autor con ese nombre");
            return false;
        } else {
            autorRepository.delete(autorEncontrado);
            log.info("Se ha eliminado el autor");
            return true;
        }
    }

    
}
