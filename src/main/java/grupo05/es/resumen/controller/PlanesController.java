package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class PlanesController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Mostrar formulario de pago
    @GetMapping("/planes")
    public String mostrarFormularioPago(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";  // Asegura que solo usuarios autenticados accedan
        }

        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "planes"; // Thymeleaf buscará planes.html
    }

    // Procesar el formulario y cambiar a LECTOR
    @PostMapping("/mejorar-a-lector")
    public String convertirEnLector(@RequestParam String titular,
                                    @RequestParam String infoBancaria,
                                    Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setRol("LECTOR");
        usuarioRepository.save(usuario);

        return "planes_confirmacion";  //
    }

    @PostMapping("/activar-escritor")
    public String activarPagoEscritor(Principal principal) {
        if (principal == null) return "redirect:/login";
    
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        // 🔒 ¡NO cambiar el rol!
        if ("ESCRITOR".equals(usuario.getRol())) {
            usuario.setHaPagado(true);
            usuarioRepository.save(usuario);
        }
    
        return "planes_confirmacion";
    }
}

