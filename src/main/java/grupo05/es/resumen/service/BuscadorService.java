package grupo05.es.resumen.service;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.repository.ResumenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscadorService implements BuscadorServiceInterface {

    @Autowired
    private ResumenRepository resumenRepository;

    @Override
    public List<Resumen> buscaResumenes(String queryText){
        return resumenRepository.findResumenesByQueryText(queryText);
    }
}
