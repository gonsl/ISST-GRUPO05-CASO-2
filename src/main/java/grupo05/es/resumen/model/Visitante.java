package grupo05.es.resumen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(name = "nombre")
    private String name;
    @Email
    @NotBlank
    @Column(name="email")
    private String email;

}
