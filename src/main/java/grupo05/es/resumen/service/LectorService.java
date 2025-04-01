package grupo05.es.resumen.service;

import grupo05.es.resumen.model.Lector;
import grupo05.es.resumen.model.LectorPrivado;
import grupo05.es.resumen.repository.LectorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LectorService {

    @Autowired
    private LectorRepository lectorRepository;


    public LectorService(LectorRepository lectorRepository) {
        this.lectorRepository = lectorRepository;
    }

    public Lector getLectorByNombre(String lectorName){
        Lector lectorEncontrado = lectorRepository.findByNombre(lectorName);
        if(lectorName==null){
            log.warn("No existe ese lector");
            return null;
        }
        log.info("Lector encontrado ");
        return lectorEncontrado;
    }

    public List<Lector> getAllLectores(){
        List<Lector> lectorList = lectorRepository.findAll();

        if(!lectorList.isEmpty()){
            log.info("Devolviendo Lectores");
            return lectorList;
        }
        log.warn("No existen Lectores");
        return lectorList;
    }

    public Boolean postLector(LectorPrivado lectorPrivado){
        if(getLectorByNombre(lectorPrivado.getNombre())==null){
            Lector lector = Lector.builder()
                    .email(lectorPrivado.getEmail())
                    .nombre(lectorPrivado.getNombre())
                    .build();

            lectorRepository.save(lector);
            log.info("Lector creado ");
            return true;
        }
        log.warn("ya existe ese lector");
        return false;
    }

    public Boolean deleteLector(String lectorName){
        Lector lectorEncontrado = getLectorByNombre(lectorName);
        if(getLectorByNombre(lectorName)!=null){
            lectorRepository.delete(lectorEncontrado);
            log.info("Lector Eliminado");
            return true;
        }
        log.warn("No existe el lector");
        return  false;
    }

    public Boolean putLector(LectorPrivado lector){
        Lector lectorEncontrado = lectorRepository.findByEmail(lector.getEmail());

        if(lectorEncontrado==null){
            log.warn("No existe el lector que quieres actualizar");
            return false;

        }else{
            lectorEncontrado.setEmail(lector.getEmail());
            lectorEncontrado.setNombre(lector.getNombre());

            log.info("Lector actualizado: " +System.lineSeparator()+
                    "E-mail: " + lectorEncontrado.getEmail()+
                    System.lineSeparator() +
                    "Name: " + lectorEncontrado.getNombre());

            return true;
        }
    }
}
