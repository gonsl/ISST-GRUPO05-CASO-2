package grupo05.es.resumen.service;

import grupo05.es.resumen.model.Autor;
import grupo05.es.resumen.model.AutorPrivado;
import grupo05.es.resumen.model.Visitante;
import grupo05.es.resumen.model.VisitantePrivado;
import grupo05.es.resumen.repository.VisitanteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VisitanteService {

    @Autowired
    private VisitanteRepository visitanteRepository;

    public List<Visitante> getAllVisitante() {
        List<Visitante> visitanteList = visitanteRepository.findAll();
        if (visitanteList.isEmpty()) {
            log.warn("No hay visitantes");
            return visitanteList;
        }
        return visitanteList;
    }

    public Visitante getVisitanteByNombre(String nombre) {
        Visitante visitanteEncontrado = visitanteRepository.findByNombre(nombre);
        if (visitanteEncontrado == null) {
            log.warn("No existe el visitante");
            return null;
        }
        return visitanteEncontrado;
    }

    public Boolean postVisitante(VisitantePrivado visitantePrivado) {
        Visitante visitante = Visitante.builder()
                .nombre(visitantePrivado.getNombre())
                .email(visitantePrivado.getEmail())
                .build();
        Visitante visitanteEncontrado = visitanteRepository.findByNombre(visitante.getNombre());
        if (visitanteEncontrado != null) {
            log.warn("No existe el visitante");
            return false;
        }
        visitanteRepository.save(visitante);
        return true;
    }

    public Boolean deleteVisitante(String nombre) {
        Visitante visitanteEncontrado = visitanteRepository.findByNombre(nombre);
        if (visitanteEncontrado == null) {
            log.warn("No existe ese visitante");
            return false;
        }
        visitanteRepository.delete(visitanteEncontrado);
        return true;
    }

    public Boolean putVisitante(VisitantePrivado visitante) {
        Visitante visitanteEncontrado = visitanteRepository.findByEmail(visitante.getEmail());
        if (visitanteEncontrado == null) {
            log.warn("No existe el visitante a actualiza");
            return false;
        }
        visitanteEncontrado.setEmail(visitante.getEmail());
        visitanteEncontrado.setNombre(visitante.getNombre());
        return true;
    }
}