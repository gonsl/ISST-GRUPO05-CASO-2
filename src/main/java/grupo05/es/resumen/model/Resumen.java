package grupo05.es.resumen.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Resumen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String autorOriginal;

    @Column(length = 5000)
    private String resumenTexto;

    private String resumenAudioUrl; // Link o path del audio

    private String escritorEmail; // Relación directa o simplificada

    private int visitas;

    private double valoracionMedia;

    private boolean gratuito;

    private String resumenArchivoTipo;

    private String categoria;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] resumenArchivo;
}
