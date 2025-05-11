package grupo05.es.resumen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String inicio() {
        return "inicio"; // Renderiza templates/inicio.html
    }
}
