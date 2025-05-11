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
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogoController.class)
@AutoConfigureMockMvc
class CatalogoControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private ResumenRepository resumenRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ValoracionRepository valoracionRepository;

    // 🔹 Test: GET /catalogo
    @WithMockUser(username = "test@example.com", roles = "LECTOR")
    @Test
    void verCatalogo_conLectorAutenticado_devuelveVistaCatalogo() throws Exception {
        List<Resumen> resumenes = List.of(new Resumen());
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setRol("LECTOR");
        usuario.setResumenesFavoritos(Set.of(new Resumen())); // tiene favoritos

        when(resumenRepository.findAll()).thenReturn(resumenes);
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/catalogo").with(user("test@example.com").roles("LECTOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("catalogo"))
                .andExpect(model().attribute("resumenes", resumenes))
                .andExpect(model().attribute("rol", "LECTOR"))
                .andExpect(model().attribute("tieneFavoritos", true));
    }

    // 🔹 Test: GET /resumen/texto/{id}
    @WithMockUser(username = "lector@example.com", roles = "LECTOR")
    @Test
    void verResumenTexto_existente_devuelveResumenDetalle() throws Exception {
        Long resumenId = 1L;
        Resumen resumen = new Resumen();
        resumen.setId(resumenId);
        List<Valoracion> valoraciones = List.of(new Valoracion());

        Usuario usuario = new Usuario();
        usuario.setEmail("lector@example.com");
        usuario.setResumenesFavoritos(Set.of(resumen));

        when(resumenRepository.findById(resumenId)).thenReturn(Optional.of(resumen));
        when(valoracionRepository.findByResumenId(resumenId)).thenReturn(valoraciones);
        when(usuarioRepository.findByEmail("lector@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/resumen/texto/" + resumenId)
                        .with(user("lector@example.com").roles("LECTOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("resumen_detalle"))
                .andExpect(model().attributeExists("resumen"))
                .andExpect(model().attributeExists("valoraciones"))
                .andExpect(model().attributeExists("valoracion"))
                .andExpect(model().attribute("esFavorito", true))
                .andExpect(model().attributeExists("usuario"));
    }

    // 🔹 Test: GET /resumen/texto/{id} inexistente
    @WithMockUser
    @Test
    void verResumenTexto_noExistente_redirigeACatalogo() throws Exception {
        when(resumenRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/resumen/texto/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalogo"));
    }
}
