package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.model.Valoracion;
import grupo05.es.resumen.repository.ResumenRepository;
import grupo05.es.resumen.repository.UsuarioRepository;
import grupo05.es.resumen.repository.ValoracionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComentarioController.class)
@AutoConfigureMockMvc
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValoracionRepository valoracionRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ResumenRepository resumenRepository;



    @WithMockUser(username = "escritor@example.com", roles = {"ESCRITOR"})
    @Test
    void subirComentario_usuarioEscritor_redirigeConError() throws Exception {
        Resumen resumen = new Resumen();
        resumen.setId(1L);
        when(resumenRepository.findById(1L)).thenReturn(Optional.of(resumen));

        Usuario escritor = new Usuario();
        escritor.setRol("ESCRITOR");
        when(usuarioRepository.findByEmail("escritor@example.com")).thenReturn(Optional.of(escritor));

        mockMvc.perform(post("/valoraciones/nueva")
                        .param("resumen.id", "1")
                        .param("comentario", "comentario")
                        .param("puntuacion", "4")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/resumen/texto/1"))
                .andExpect(flash().attributeExists("message"));
    }

    @WithMockUser(username = "lector@example.com", roles = {"LECTOR"})
    @Test
    void subirComentario_valido_guardaYRedirige() throws Exception {
        Resumen resumen = new Resumen();
        resumen.setId(1L);
        when(resumenRepository.findById(1L)).thenReturn(Optional.of(resumen));

        Usuario lector = new Usuario();
        lector.setEmail("lector@example.com");
        lector.setRol("LECTOR");
        when(usuarioRepository.findByEmail("lector@example.com")).thenReturn(Optional.of(lector));

        Valoracion v = new Valoracion();
        v.setComentario("Buen resumen");
        v.setPuntuacion(5);
        v.setResumen(resumen);
        v.setUsuarioEmail("lector@example.com");

        when(valoracionRepository.findByResumenId(1L)).thenReturn(List.of(v));

        mockMvc.perform(post("/valoraciones/nueva")
                        .param("resumen.id", "1")
                        .param("comentario", "Buen resumen")
                        .param("puntuacion", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/resumen/texto/1"));

        verify(valoracionRepository).save(any(Valoracion.class));
        verify(resumenRepository).save(any(Resumen.class));
    }
}
