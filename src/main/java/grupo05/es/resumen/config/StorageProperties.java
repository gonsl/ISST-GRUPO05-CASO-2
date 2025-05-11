package grupo05.es.resumen.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Getter
@Setter
@ConfigurationProperties("storage")
@Configuration
public class StorageProperties {

    private String location = "upload-dir";




}
