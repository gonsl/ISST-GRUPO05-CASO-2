package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.repository.UsuarioRepository;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;



    // 🔹 GET /registro
    @WithMockUser
    @Test
    void mostrarFormularioRegistro_deberiaCargarVistaRegistroConUsuario() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("usuario"));
    }

    // 🔹 POST /registro (rol válido)
    @WithMockUser
    @Test
    void procesarRegistro_conRolValido_redirigeALogin() throws Exception {
        when(passwordEncoder.encode("1234")).thenReturn("hashed");

        mockMvc.perform(post("/registro")
                        .param("email", "user@example.com")
                        .param("password", "1234")
                        .param("rol", "ESCRITOR")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(usuarioRepository).save(any(Usuario.class));
    }

    // 🔹 POST /registro (rol inválido)





    // 🔹 GET /perfil (escritor no ha pagado)
    @WithMockUser(username = "escritor@example.com", roles = "ESCRITOR")
    @Test
    void cargarInfoPerfil_escritorNoPagado_redirigeAPago() throws Exception {
        Usuario escritor = new Usuario();
        escritor.setEmail("escritor@example.com");
        escritor.setRol("ESCRITOR");
        escritor.setHaPagado(false);

        when(usuarioRepository.findByEmail("escritor@example.com")).thenReturn(Optional.of(escritor));

        mockMvc.perform(get("/perfil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pago_autor"));
    }

    // 🔹 GET /perfil (lector con favoritos)
    @WithMockUser(username = "lector@example.com", roles = "LECTOR")
    @Test
    void cargarInfoPerfil_lectorConFavoritos_devuelveVistaPerfil() throws Exception {
        Usuario lector = new Usuario();
        lector.setEmail("lector@example.com");
        lector.setRol("LECTOR");
        lector.setHaPagado(true);
        lector.setResumenesFavoritos(Set.of(new Resumen()));

        when(usuarioRepository.findByEmail("lector@example.com")).thenReturn(Optional.of(lector));

        mockMvc.perform(get("/perfil"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("favoritos"));
    }

    // 🔹 POST /cambiar-rol (VISITANTE → LECTOR)
    @WithMockUser(username = "visitante@example.com")
    @Test
    void cambiarRol_visitanteACambiaAlector_redirigeAPerfil() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("visitante@example.com");
        usuario.setRol("VISITANTE");

        when(usuarioRepository.findByEmail("visitante@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/cambiar-rol")
                        .param("rolActual", "VISITANTE")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        assertEquals("LECTOR", usuario.getRol());
        verify(usuarioRepository).save(usuario);
    }
}

