package grupo05.es.resumen.controller;

import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.repository.ResumenRepository;
import grupo05.es.resumen.repository.UsuarioRepository;
import grupo05.es.resumen.service.BuscadorService;
import grupo05.es.resumen.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@AutoConfigureMockMvc
public class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    @MockBean
    private ResumenRepository resumenRepository;

    @MockBean
    private BuscadorService buscadorService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @WithMockUser(username = "usuario", roles = {"ESCRITOR"})
    @Test
    void mostrarFormulario_deberiaMostrarFormularioSubida() throws Exception {
        mockMvc.perform(get("/pruebas/files/subir"))
                .andExpect(status().isOk())
                .andExpect(view().name("uploadForm"))
                .andExpect(model().attributeExists("resumen"));
    }
    @WithMockUser
    @Test
    void serveFile_archivoExiste_devuelveArchivo() throws Exception {
        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn("archivo.pdf");
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream("contenido".getBytes()));

        when(storageService.loadAsResource("archivo.pdf")).thenReturn(mockResource);

        mockMvc.perform(get("/pruebas/files/archivo.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"archivo.pdf\""));
    }


    @WithMockUser(username = "test@example.com", roles = {"ESCRITOR"})
    @Test
    void guardarResumenEnBD_archivoVacio_redireccionaConError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/pruebas/files/subir/bd/post")
                        .file(file)
                        .with(csrf())) // NECESARIO PARA EVITAR 403
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pruebas/subir"))
                .andExpect(flash().attributeExists("message"));
    }



    @WithMockUser(username = "test@example.com", roles = {"ESCRITOR"})
    @Test
    void guardarResumenEnBD_noEsPDF_redireccionaConError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "archivo.txt", "text/plain", "contenido".getBytes());

        mockMvc.perform(multipart("/pruebas/files/subir/bd/post")
                        .file(file)
                        .with(csrf())) // NECESARIO
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pruebas/subir"))
                .andExpect(flash().attributeExists("message"));
    }






}
