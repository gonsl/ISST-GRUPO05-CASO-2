package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.model.Valoracion;
import grupo05.es.resumen.repository.ResumenRepository;
import grupo05.es.resumen.repository.UsuarioRepository;

import grupo05.es.resumen.repository.ValoracionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Controller
public class CatalogoController {

    private final ResumenRepository resumenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValoracionRepository valoracionRepository;

    public CatalogoController(ResumenRepository resumenRepository, UsuarioRepository usuarioRepository,
            ValoracionRepository valoracionRepository) {
        this.resumenRepository = resumenRepository;
        this.usuarioRepository = usuarioRepository;
        this.valoracionRepository = valoracionRepository;
    }


    @GetMapping("/catalogo")
    public String verCatalogo(Model model, Authentication auth, Principal principal) {
        // 1) Siempre traemos todos los resúmenes
        List<Resumen> resúmenes = resumenRepository.findAll();
        //Enviamos si la lista de favoritos esta o no vacia para mostrar el boton de favoritos
        Usuario usuarioEncontrado = usuarioRepository.findByEmail(principal.getName()).get();
        Boolean tieneFavoritos = !usuarioEncontrado.getResumenesFavoritos().isEmpty();
        model.addAttribute("tieneFavoritos", tieneFavoritos);
        // 2) Inyectamos también info de rol/autenticación si la necesitas en la vista
        model.addAttribute("resumenes", resúmenes);
        String rol;
        if (auth == null || !auth.isAuthenticated()) {
            rol = "VISITANTE";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LECTOR"))) {
            rol = "LECTOR";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ESCRITOR"))) {
            rol = "ESCRITOR";
            if (principal != null) {
                Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElse(null);
                if (usuario != null && !usuario.isHaPagado()) {
                    return "pago_autor"; // Bloqueo
                }
            }
        } else {
            rol = "VISITANTE"; // Fallback en caso de que no coincida
        }
    
        model.addAttribute("rol", rol);
    
        return "catalogo";
    }
    

    @GetMapping("/resumen/texto/{id}")
public String verResumenTexto(@PathVariable Long id, Model model, Principal principal) {
    Optional<Resumen> opt = resumenRepository.findById(id);
    if (opt.isPresent()) {
        Resumen resumen = opt.get();
        model.addAttribute("resumen", resumen);

        // Mostrar Lista de Valoraciones
        List<Valoracion> valoracionList = valoracionRepository.findByResumenId(id);
        model.addAttribute("valoraciones", valoracionList);

        // Nueva Valoración vacía (preparada con el resumen asignado)
        Valoracion nuevaValoracion = new Valoracion();
        nuevaValoracion.setResumen(resumen);
        model.addAttribute("valoracion", nuevaValoracion);

        Usuario usuarioEncontrado  = usuarioRepository.findByEmail(principal.getName()).get();
        Boolean esFavorito = usuarioEncontrado.getResumenesFavoritos().contains(resumen);
        model.addAttribute("esFavorito", esFavorito);

        // Cargar usuario autenticado, si hay
        if (principal != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(principal.getName());
            usuarioOpt.ifPresent(usuario -> model.addAttribute("usuario", usuario));
        }

        return "resumen_detalle";
    }

    return "redirect:/catalogo";
}


}
