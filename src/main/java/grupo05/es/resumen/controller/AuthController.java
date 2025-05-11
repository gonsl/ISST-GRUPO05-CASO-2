package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;


@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Procesar el formulario de registro
    @PostMapping("/registro")
public String procesarRegistro(@ModelAttribute Usuario usuario) {
    String rol = usuario.getRol();

    if (!rol.equals("VISITANTE") && !rol.equals("ESCRITOR")) {
        return "redirect:/registro";
    }

    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.setHaPagado(false); // MUY IMPORTANTE para escritores
    usuarioRepository.save(usuario);

    return "redirect:/login";
}
    @GetMapping("/perfil")
    public String cargarInfoPerfil(Model model, Principal principal) {
        // 1. Obtener usuario autenticado
    if (principal == null) {
        return "redirect:/login";
    }

    Usuario usuario = usuarioRepository.findByEmail(principal.getName()).orElse(null);
    if (usuario == null) {
        return "redirect:/login";
    }

    // 2. Restringir ESCRITORES que no han pagado
    if ("ESCRITOR".equals(usuario.getRol()) && !usuario.isHaPagado()) {
        return "redirect:/pago_autor";
    }

    // 3. Cargar datos del usuario
    model.addAttribute("usuario", usuario);

    // 4. Si es lector, también cargar favoritos
    if ("LECTOR".equals(usuario.getRol())) {
        model.addAttribute("favoritos", usuario.getResumenesFavoritos());
    }

    // 5. Mostrar la vista perfil
    return "perfil";
    }

    @PostMapping("/cambiar-rol")
    public String cambiarRol(@RequestParam String rolActual, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        switch (rolActual) {
            case "VISITANTE" -> usuario.setRol("LECTOR");
            case "LECTOR", "ESCRITOR" -> usuario.setRol("VISITANTE");
        }
    
        usuarioRepository.save(usuario);
        return "redirect:/perfil";
    }
    



}
