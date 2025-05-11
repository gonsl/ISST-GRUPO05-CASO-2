package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.model.Valoracion;
import grupo05.es.resumen.repository.ResumenRepository;
import grupo05.es.resumen.repository.UsuarioRepository;
import grupo05.es.resumen.repository.ValoracionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/valoraciones")
public class ComentarioController {

    private final ValoracionRepository valoracionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ResumenRepository resumenRepository;

    public ComentarioController(ValoracionRepository valoracionRepository, UsuarioRepository usuarioRepository,
            ResumenRepository resumenRepository) {
        this.valoracionRepository = valoracionRepository;
        this.usuarioRepository = usuarioRepository;
        this.resumenRepository = resumenRepository;
    }

    @PostMapping("/nueva")
    public String subirComentario(@ModelAttribute Valoracion valoracion,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Optional<Resumen> resumenOpt = resumenRepository.findById(valoracion.getResumen().getId());
        Resumen resumen = resumenOpt.get();
        if(resumen==null){
            redirectAttributes.addFlashAttribute("message", "No se encuentra el resumen??");
            return "redirect:/catalogo";
        }
        Long resumenID = resumen.getId();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(principal.getName());
        if(Objects.equals(usuarioOpt.get().getRol(), "ESCRITOR")){
            redirectAttributes.addFlashAttribute("message", "Los escritores no pueden comentar resumenes");
            return "redirect:/resumen/texto/"+ resumenID;
        }
        //Guardamos la valoracion realmente el comentario y la puntuacion no haria falta al pasarselas en el th
        valoracion.setResumen(resumen);
        valoracion.setComentario(valoracion.getComentario());
        valoracion.setUsuarioEmail(principal.getName());
        valoracion.setPuntuacion(valoracion.getPuntuacion());
        valoracionRepository.save(valoracion);

        //Guardamos la valoracion al resumen
        List<Valoracion> valoracionList = valoracionRepository.findByResumenId(resumenID);
        double media = valoracionList.stream().mapToInt(Valoracion::getPuntuacion).average().orElse(0.0);
        resumen.setValoracionMedia(media);
        resumenRepository.save(resumen);
        return "redirect:/resumen/texto/" + resumenID;
    }
}
