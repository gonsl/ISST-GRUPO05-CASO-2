package grupo05.es.resumen.config;

public class StorageFileNotFoundException extends StorageException{

    public StorageFileNotFoundException(String message){
        super(message);
    }
    public StorageFileNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
