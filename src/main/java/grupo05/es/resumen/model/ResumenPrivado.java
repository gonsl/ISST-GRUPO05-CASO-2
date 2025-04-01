package grupo05.es.resumen.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class ResumenPrivado {


    private String titulo;

    private String descripcion;

    private Boolean prime;

    private String contenido;


    private String autor;
}
