package grupo05.es.resumen.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "visitante")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Visitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre")
    private String name;
    @Column(name="email")
    private String email;

}
