package grupo05.es.resumen.service;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path laod(String fileName);

    Resource loadAsResource(String fileName);

    void deleteAll();
}
