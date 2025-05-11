package grupo05.es.resumen.service;

import grupo05.es.resumen.config.StorageException;
import grupo05.es.resumen.config.StorageFileNotFoundException;
import grupo05.es.resumen.config.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService  implements StorageService{

    private Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties){
        if (properties.getLocation().trim().length()==0){
            throw new StorageException("La localizacion del archivo no puede estar vacio");
        }
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file){
        try{
            if(file.isEmpty()){
                throw new StorageException("No se ha podido guardar el archivo");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())){
                throw new StorageException("No se puede guardar el archivo en el directorio actual");
            }
            try(InputStream inputStream = file.getInputStream()){
                Files.createDirectories(this.rootLocation);
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e){
            throw new StorageException("Error al guardar el archivo", e);
        }
    }

    @Override
    public Stream<Path> loadAll(){
        try {
            return Files.walk(this.rootLocation,1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e){
            throw new StorageException("Error al leer los archivos guardados", e);
        }
    }

    @Override
    public Path laod(String fileName) {
        return rootLocation.resolve(fileName);
    }

    @Override
    public Resource loadAsResource(String fileName){
        try {
            Path file = laod(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable() ){
                return resource;
            }else{
                throw new StorageFileNotFoundException("No se puede leer el archivo "+fileName);
            }
        }
        catch (MalformedURLException e){
            throw new StorageFileNotFoundException("No se puede leer el archive", e);
        }
    }


    @Override
    public void deleteAll(){
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init(){
        try{
            Files.createDirectories(rootLocation);
        }
        catch (IOException e){
            throw new StorageException("No se puede inicializar el almacenamiento de archivos", e);
        }
    }
}
