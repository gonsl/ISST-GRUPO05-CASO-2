package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.repository.ResumenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/escritor")
public class EscritorController {

    private final ResumenRepository resumenRepository;

    public EscritorController(ResumenRepository resumenRepository) {
        this.resumenRepository = resumenRepository;
    }

    // Mostrar formulario para subir nuevo resumen
    @GetMapping("/subir")
    public String mostrarFormulario(Model model) {
        model.addAttribute("resumen", new Resumen());
        return "subir_resumen";
    }

    // Procesar formulario
    @PostMapping("/subir")
    public String procesarFormulario(@ModelAttribute Resumen resumen, Principal principal) {
        resumen.setEscritorEmail(principal.getName());
        resumen.setValoracionMedia(0.0);
        resumen.setVisitas(0);
        resumenRepository.save(resumen);
        return "redirect:/escritor/mis-resumenes";
    }

    // Ver resúmenes subidos por este escritor
    @GetMapping("/mis-resumenes")
    public String verMisResumenes(Model model, Principal principal) {
        List<Resumen> lista = resumenRepository.findByEscritorEmail(principal.getName());
        model.addAttribute("resumenes", lista);
        return "mis_resumenes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarResumen(@PathVariable Long id, Principal principal){
        Optional<Resumen> resumenOpt = resumenRepository.findById(id);
        if(resumenOpt.isPresent()){
            Resumen resumen = resumenOpt.get();
            if (resumen.getEscritorEmail().equals(principal.getName())){
                resumenRepository.delete(resumen);
            }
        }
            return "redirect:/escritor/mis-resumenes";

    }

    @GetMapping("/editar/{id}")
public String mostrarFormularioEdicion(@PathVariable Long id, Model model, Principal principal) {
    Optional<Resumen> opt = resumenRepository.findById(id);
    if (opt.isPresent() && opt.get().getEscritorEmail().equals(principal.getName())) {
        model.addAttribute("resumen", opt.get());
        return "editar_resumen"; // nombre de tu template
    }
    return "redirect:/escritor/mis-resumenes";
}

@PostMapping("/editar/{id}")
public String guardarCambiosEdicion(@PathVariable Long id,
                                    @ModelAttribute Resumen resumenActualizado,
                                    Principal principal,
                                    Model model) {
    Optional<Resumen> opt = resumenRepository.findById(id);
    if (opt.isPresent() && opt.get().getEscritorEmail().equals(principal.getName())) {
        Resumen resumen = opt.get();
        resumen.setTitulo(resumenActualizado.getTitulo());
        resumen.setAutorOriginal(resumenActualizado.getAutorOriginal());
        resumen.setResumenTexto(resumenActualizado.getResumenTexto());
        resumen.setCategoria(resumenActualizado.getCategoria());
        resumen.setGratuito(resumenActualizado.isGratuito());
        resumenRepository.save(resumen);
    }
    return "redirect:/escritor/mis-resumenes";
}

}
