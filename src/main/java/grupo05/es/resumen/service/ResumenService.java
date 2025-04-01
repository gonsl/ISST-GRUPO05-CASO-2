package grupo05.es.resumen.service;

import grupo05.es.resumen.model.Autor;
import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.model.ResumenPrivado;
import grupo05.es.resumen.repository.AutorRepository;
import grupo05.es.resumen.repository.ResumenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumenService {

    private final AutorRepository autorRepository;
    private final ResumenRepository resumenRepository;

    public List<Resumen> getAllResumenes() {
        List<Resumen> resumenList = resumenRepository.findAll();
        if(resumenList.isEmpty()){
            log.warn("No hay resumen en la bd");
            return resumenList;
        }
        return resumenList;

    }

    public Resumen getResumenByNombre(String nombre){
        Resumen resumen = resumenRepository.findByTitulo(nombre);
        if(resumen==null){
            log.warn("no existe ese resumen");
            return null;
        }
        return resumen;
    }


    public Resumen getResumenById(Integer id) {

        Optional<Resumen> resumen = resumenRepository.findById(id);
        if(resumen.isEmpty()){
            log.warn("No existe ese resumen");
            return null;
        }
        return resumen.get();
    }




    public Boolean deleteResumen(String nombre){
        Resumen resumen = resumenRepository.findByTitulo(nombre);
        if(resumen==null){
            log.warn("no existe ese resumen");
            return false;
        }
        resumenRepository.delete(resumen);
        return true;
    }

    public Boolean putResumen(ResumenPrivado resumen){
        Resumen resumenEncontrado = resumenRepository.findByTitulo(resumen.getTitulo());
        Autor autor = autorRepository.findByNombre(resumen.getTitulo());
        if(resumenEncontrado==null){
            log.warn("No existe ese resumen que quieres actualizar");
            return false;
        }
        if(autor==null){
            log.warn("No existe el autor");
            return false;
        }
        resumenEncontrado.setAutor(autor);
        resumenEncontrado.setContenido(resumen.getContenido());
        resumenEncontrado.setTitulo(resumen.getTitulo());
        resumenEncontrado.setPrime(resumen.getPrime());
        resumenEncontrado.setDescripcion(resumen.getDescripcion());
        return true;
    }
    public Boolean postResumen(ResumenPrivado resumenPrivado){
        Autor autor = autorRepository.findByNombre(resumenPrivado.getAutor());
        if(autor==null){
            log.warn("No existe el autor");
            return false;
        }
        if(resumenRepository.findByTitulo(resumenPrivado.getTitulo())!=null){
            log.warn("Ya existe ese Resumen");
        }
        Resumen resumen = Resumen.builder()
                .prime(resumenPrivado.getPrime())
                .autor(autor)
                .contenido(resumenPrivado.getContenido())
                .descripcion(resumenPrivado.getDescripcion())
                .build();
        resumenRepository.save(resumen);
        return true;
    }
}

