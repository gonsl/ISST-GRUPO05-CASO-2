package grupo05.es.resumen.controller;

import grupo05.es.resumen.config.StorageFileNotFoundException;
import grupo05.es.resumen.config.StorageProperties;
import grupo05.es.resumen.model.Resumen;
import grupo05.es.resumen.model.Usuario;
import grupo05.es.resumen.repository.ResumenRepository;
import grupo05.es.resumen.repository.UsuarioRepository;
import grupo05.es.resumen.service.BuscadorService;
import grupo05.es.resumen.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@Controller
public class FileController {

    private final StorageService storageService;
    private final ResumenRepository resumenRepository;
    private final BuscadorService buscadorService;
    private final UsuarioRepository usuarioRepository;

    private String obtenerRol(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return "VISITANTE";
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LECTOR"))) return "LECTOR";
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ESCRITOR"))) return "ESCRITOR";
        return "VISITANTE";
    }
    

    @Autowired
    public FileController(StorageService storageService, ResumenRepository resumenRepository,
            BuscadorService buscadorService, UsuarioRepository usuarioRepository) {
        this.storageService = storageService;
        this.resumenRepository = resumenRepository;
        this.buscadorService = buscadorService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/pruebas/files/subir")
    public String mostrarFormulario(Model model){
        model.addAttribute("resumen", new Resumen());
        return "uploadForm";
    }

    @GetMapping("/pruebas/files/get")
    public String listUploadedFiles(Model model) throws IOException{
        List<Resumen> listaResumenes = resumenRepository.findAll();
        model.addAttribute("resumenes", listaResumenes);

       /* model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileController.class,
                        "serveFile",
                        path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));*/
        return "lista_resumenes";
    }

    @GetMapping("/pruebas/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename){
        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/pruebas/files/subir/post")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

//    @PostMapping("/pruebas/files/subir/bd/post")
//public String guardarResumenEnBD(@ModelAttribute Resumen resumen,
//        @RequestParam(value = "file", required = false) MultipartFile file,
//        Principal principal,
//        RedirectAttributes redirectAttributes) {
//    try {
//        if (file != null && !file.isEmpty()) {
//            resumen.setResumenArchivo(file.getBytes());
//            resumen.setResumenArchivoTipo(file.getContentType());
//        } else {
//            resumen.setResumenArchivo(null);
//            resumen.setResumenArchivoTipo(null);
//        }
//
//        resumen.setVisitas(0);
//        resumen.setValoracionMedia(0.0);
//        resumen.setEscritorEmail(principal.getName());
//        resumenRepository.save(resumen);
//        redirectAttributes.addFlashAttribute("message", "Resumen guardado con éxito");
//        return "redirect:/escritor/mis-resumenes";
//    } catch (IOException e) {
//        redirectAttributes.addFlashAttribute("message", "Error al guardar el resumen: " + e.getMessage());
//        return "redirect:/escritor/mis-resumenes";
//    }
//}
//SUBIR ARCHIVOS A BD
//Guardar ficheros primero se llama al metodo get que llamara a este otro
@PostMapping("/pruebas/files/subir/bd/post")
public String guardarResumenEnBD(@ModelAttribute Resumen resumen,
        @RequestParam("file") MultipartFile file,
        Principal principal,
        @RequestParam(value = "audioFile", required = false) MultipartFile audioFile,
        RedirectAttributes redirectAttributes){
    try{
        if(file.isEmpty()){
            redirectAttributes.addFlashAttribute("message", "Se ha de seleccionar un archivo");
            return "redirect:/pruebas/subir";
        }
        //Comprobamos si es un PDF
        String nombreArchivo = file.getOriginalFilename();
        if(nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".pdf")){
            redirectAttributes.addFlashAttribute("message", "Solo se permite subir un pdf");
            return "redirect:/pruebas/subir";
        }
        //Leemos el contenido y lo guardamos en un string
        try(PDDocument document = PDDocument.load(file.getInputStream())){
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            String texto = pdfTextStripper.getText(document);
            resumen.setResumenTexto(texto);
        }
        //Subida de audio (Opcional)
        if(audioFile != null && !audioFile.isEmpty()){
            Path audioFolder = Paths.get("upload/audios");
            Files.createDirectories(audioFolder);

            String audioFileName = UUID.randomUUID() + "_" + audioFile.getOriginalFilename();
            Path audioPath = audioFolder.resolve(audioFileName);
            Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);
            resumen.setResumenAudioUrl(audioFileName);
        }


        resumen.setResumenArchivo(file.getBytes());
        resumen.setVisitas(0);
        resumen.setValoracionMedia(0.0);
        resumen.setResumenArchivoTipo(file.getContentType());
        resumen.setEscritorEmail(principal.getName());

        resumenRepository.save(resumen);
        redirectAttributes.addFlashAttribute("message", "Resumen guardado con exito");
        return "redirect:/escritor/mis-resumenes";
    }
    catch (IOException e){
        redirectAttributes.addFlashAttribute("message", "Error al guardar el resumen" + e.getMessage());
        return "redirect:/escritor/mis-resumenes";
    }
}


    //!Descarga de ficheros
    @GetMapping("/pruebas/files/archivo/{titulo}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable String titulo){
        Optional<Resumen> resumenOpt = resumenRepository.findByTitulo(titulo);

        if(resumenOpt.isEmpty() || resumenOpt.get().getResumenArchivo()==null){
            return ResponseEntity.notFound().build();
        }
        Resumen resumen = resumenOpt.get();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=/" +resumen.getTitulo()+"/")
                .contentType(MediaType.parseMediaType(resumen.getResumenArchivoTipo()))
                .body(resumen.getResumenArchivo());
    }
    //AÑADE RESUMEN A FAVORITOS
    @PostMapping("/resumen/favorito/{id}")
    public String meteEnFavoritos(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(principal.getName());
        Optional<Resumen> optResumen = resumenRepository.findById(id);
    
        if (optUsuario.isPresent() && optResumen.isPresent()) {
            Usuario usuario = optUsuario.get();
            Resumen resumen = optResumen.get();
    
            if (!"LECTOR".equals(usuario.getRol())) {
                redirectAttributes.addFlashAttribute("error", "Solo los lectores pueden añadir favoritos.");
                return "redirect:/resumen/texto/" + id;
            }
    
            if (!usuario.getResumenesFavoritos().contains(resumen)) {
                usuario.getResumenesFavoritos().add(resumen);
                usuarioRepository.save(usuario);
                redirectAttributes.addFlashAttribute("message", "Resumen añadido a favoritos.");
            } else {
                redirectAttributes.addFlashAttribute("message", "Este resumen ya está en tus favoritos.");
            }
        }
    
        return "redirect:/resumen/texto/" + id;
    }

    //Escuchar el audio
    @GetMapping("/pruebas/escuchar/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveAudio(@PathVariable String filename){
        try{
            Path file =Paths.get("upload/audios/").resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(!resource.exists()){
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(file);
            if(contentType==null){
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource  + resource.getFilename() + "\"")
                    .body(resource);
        }catch (IOException e){
            return ResponseEntity.notFound().build();
        }
    }


    //Buscador por titulo, autor, contenido y autorOriginal
    @GetMapping("/busca")
public String buscaResumenes(@RequestParam String queryText, Model model, Authentication auth) {
    List<Resumen> resumenesEncontrados = buscadorService.buscaResumenes(queryText);
    model.addAttribute("resumenes", resumenesEncontrados);
    model.addAttribute("queryText", queryText);
    model.addAttribute("rol", obtenerRol(auth)); // 👈 importante
    return "catalogo";
}

@GetMapping("/filtraCategorias")
public String filtraCategorias(@RequestParam String categoria, Model model, Authentication auth) {
    Optional<List<Resumen>> resumenesEncontrados = resumenRepository.findByCategoria(categoria);
    model.addAttribute("resumenes", resumenesEncontrados.orElse(List.of()));
    model.addAttribute("categoria", categoria);
    model.addAttribute("rol", obtenerRol(auth)); // 👈 importante
    return "catalogo";
}

@GetMapping("/filtraValorados")
public String filtraValorados(Model model, Authentication auth) {
    Optional<List<Resumen>> resumenesOrdenados = resumenRepository.findAllByOrderByValoracionMediaDesc();
    model.addAttribute("resumenes", resumenesOrdenados.orElse(List.of()));
    model.addAttribute("orderedBy", "valoracionMedia");
    model.addAttribute("rol", obtenerRol(auth)); // 👈 importante
    return "catalogo";
}
    @GetMapping("/filtraFavoritos")
    public String filtraFavoritos(Model model, Principal principal, Authentication auth){
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        Set<Resumen> resumenesFavoritos = usuario.getResumenesFavoritos();
        model.addAttribute("resumenes", resumenesFavoritos);
        model.addAttribute("rol", obtenerRol(auth)); // 👈 importante
        return "catalogo";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
    //Dar las recomendaciones al cliente
    @GetMapping("/recomendaciones")
    public String obtenerRecomendacionesConLibro(Resumen resumen,
                                                Model model,
                                                 Authentication auth) {
        String categoria = resumen.getCategoria();
        List<Resumen> resumenesRecomendados = resumenRepository.findByCategoria(categoria).get();
        model.addAttribute("rol", obtenerRol(auth));
        model.addAttribute("resumenes", resumenesRecomendados);
        return "catalogo";
    }
    //Ofrecer 5 resumenes para elegir categoria
    @GetMapping("/escogeResumen")
        public String escogerResumen(Model model, Authentication auth){
        String contenidoResumen = "Este es el contenido de prueba de un fichero PDF";
        //Creacion de un escritor inicial
        Usuario escritor =  new Usuario();
        escritor.setEmail("EscogeTuResumen@gmail.com");
        escritor.setRol("ESCRITOR");
        escritor.setSuscrito(false);
        escritor.setPassword("1234");
        escritor.setNombre("EscogeTuResumen");
        usuarioRepository.save(escritor);

        //Creacion de un Lector Inicial
        Usuario lector = new Usuario();
        lector.setNombre("EscogeIncial");
        lector.setPassword("1234");
        lector.setRol("LECTOR");
        lector.setEmail("EscogeIncial@gmail.com");
        usuarioRepository.save(lector);

        List<Resumen> resumenesAEscoger = new ArrayList<>();

        //Creacion de resumen inicial
        Resumen resumen = new Resumen();
        resumen.setVisitas(2);
        resumen.setValoracionMedia(2.5);
        resumen.setResumenTexto(contenidoResumen);
        resumen.setResumenArchivo(contenidoResumen.getBytes());
        resumen.setResumenAudioUrl("3f9c5115-098e-498f-8074-263c6b2cf023_town-10169.mp3");
        resumen.setEscritorEmail("escritoInicial@gmail.com");
        resumen.setAutorOriginal("Henry Hazlitt");
        resumen.setCategoria("Economia");
        resumen.setResumenArchivoTipo("application/pdf");
        resumen.setGratuito(true);
        resumen.setTitulo("Economia en una leccion");
        resumenesAEscoger.add(resumen);

        Resumen resumen2 = new Resumen();
        resumen2.setVisitas(2);
        resumen2.setValoracionMedia(2.5);
        resumen2.setResumenTexto(contenidoResumen);
        resumen2.setResumenArchivo(contenidoResumen.getBytes());
        resumen2.setResumenAudioUrl("3f9c5115-098e-498f-8074-263c6b2cf023_town-10169.mp3");
        resumen2.setEscritorEmail("escritoInicial@gmail.com");
        resumen2.setAutorOriginal("Robert T. Kiyosaki");
        resumen2.setResumenArchivoTipo("application/pdf");
        resumen2.setGratuito(true);
        resumen2.setCategoria("Negocios");
        resumen2.setTitulo("Padre rico padre pobre");
        resumenesAEscoger.add(resumen2);


        Resumen resumen3 = new Resumen();
        resumen3.setVisitas(2);
        resumen3.setValoracionMedia(2.5);
        resumen3.setResumenTexto(contenidoResumen);
        resumen3.setResumenArchivo(contenidoResumen.getBytes());
        resumen3.setResumenAudioUrl("3f9c5115-098e-498f-8074-263c6b2cf023_town-10169.mp3");
        resumen3.setEscritorEmail("escritoInicial@gmail.com");
        resumen3.setAutorOriginal("Spencer Johnson");
        resumen3.setResumenArchivoTipo("application/pdf");
        resumen3.setGratuito(true);
        resumen3.setCategoria("Autoayuda");
        resumen3.setTitulo("Quien se ha llevado mi queso");
        resumenesAEscoger.add(resumen3);

        Resumen resumen4 = new Resumen();
        resumen4.setVisitas(2);
        resumen4.setValoracionMedia(2.5);
        resumen4.setResumenTexto(contenidoResumen);
        resumen4.setResumenArchivo(contenidoResumen.getBytes());
        resumen4.setResumenAudioUrl("3f9c5115-098e-498f-8074-263c6b2cf023_town-10169.mp3");
        resumen4.setEscritorEmail("escritoInicial@gmail.com");
        resumen4.setAutorOriginal("John C. Maxwell");
        resumen4.setResumenArchivoTipo("application/pdf");
        resumen4.setGratuito(true);
        resumen4.setCategoria("Liderazgo");
        resumen4.setTitulo("Las 21 leyes irrefutables del liderazgo");
        resumenesAEscoger.add(resumen4);

        model.addAttribute("resumenes", resumenesAEscoger);
        model.addAttribute("rol", obtenerRol(auth));
        return "recomendaciones";
    }
}
