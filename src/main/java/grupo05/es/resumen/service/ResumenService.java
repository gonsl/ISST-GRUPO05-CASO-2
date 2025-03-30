package grupo05.es.resumen.service;

import grupo05.es.resumen.model.Resumen;
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

    private final ResumenRepository resumenRepository;

    public List<Resumen> getAllResumenes() {
        return resumenRepository.findAll();
    }

    public Optional<Resumen> getResumenById(Integer id) {
        return resumenRepository.findById(id);
    }

    public void guardarResumen(Resumen resumen) {
        resumenRepository.save(resumen);
    }
    
}

