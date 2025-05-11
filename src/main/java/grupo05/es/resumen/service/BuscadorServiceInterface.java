package grupo05.es.resumen.service;

import grupo05.es.resumen.model.Resumen;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.util.List;

public interface BuscadorServiceInterface {
    List<Resumen> buscaResumenes(String queryText);
}
