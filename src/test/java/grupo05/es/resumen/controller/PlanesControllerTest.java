package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanesController.class)
@AutoConfigureMockMvc
class PlanesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    // 🔹 GET /planes (usuario no autenticado)

    // 🔹 GET /planes (usuario autenticado)
    @WithMockUser(username = "test@example.com")
    @Test
    void mostrarFormularioPago_conPrincipal_devuelveVistaPlanes() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");

        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/planes"))
                .andExpect(status().isOk())
                .andExpect(view().name("planes"))
                .andExpect(model().attributeExists("usuario"));
    }

    // 🔹 POST /mejorar-a-lector (no autenticado)

    // 🔹 POST /mejorar-a-lector (autenticado)
    @WithMockUser(username = "visitante@example.com")
    @Test
    void convertirEnLector_conUsuario_redirigeAConfirmacion() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("visitante@example.com");
        usuario.setRol("VISITANTE");

        when(usuarioRepository.findByEmail("visitante@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/mejorar-a-lector")
                        .param("titular", "John Doe")
                        .param("infoBancaria", "123456")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("planes_confirmacion"));

        assertEquals("LECTOR", usuario.getRol());
        verify(usuarioRepository).save(usuario);
    }

    // 🔹 POST /activar-escritor (sin login)


    // 🔹 POST /activar-escritor (usuario escritor)
    @WithMockUser(username = "escritor@example.com", roles = "ESCRITOR")
    @Test
    void activarPagoEscritor_conEscritorMarcaPagado() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("escritor@example.com");
        usuario.setRol("ESCRITOR");
        usuario.setHaPagado(false);

        when(usuarioRepository.findByEmail("escritor@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/activar-escritor").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("planes_confirmacion"));

        assertTrue(usuario.isHaPagado());
        verify(usuarioRepository).save(usuario);
    }

    // 🔹 POST /activar-escritor (usuario NO escritor)
    @WithMockUser(username = "lector@example.com", roles = "LECTOR")
    @Test
    void activarPagoEscritor_usuarioNoEscritor_noModifica() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("lector@example.com");
        usuario.setRol("LECTOR");

        when(usuarioRepository.findByEmail("lector@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/activar-escritor").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("planes_confirmacion"));

        assertFalse(usuario.isHaPagado()); // No cambia
        verify(usuarioRepository, never()).save(any());
    }
}
