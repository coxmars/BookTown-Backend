package pe.todotic.bookstoreapi_s2.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pe.todotic.bookstoreapi_s2.exception.MediaFileNotFoundException;
import pe.todotic.bookstoreapi_s2.exception.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${storage.location}")
    private String storageLocation;

    // Se usa esta anotacion para llamar init automaticamente o apenas se necesite para crear la carpeta
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(storageLocation));
        } catch (IOException ex) {
            throw new StorageException("No se pudo crear el almacén de archivos: " + storageLocation);
        }
    }

    public String store(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalFilename);
        if (file.isEmpty()) {
            throw new StorageException("No se puede almacenar un archivo vacío " + originalFilename);
        }
        try {
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, Paths.get(storageLocation).resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new StorageException("Falló al almacenar el archivo " + originalFilename);
        }
        return filename;
    }

    public Resource loadAsResource(String filename) {
        try {
            Path path = Paths.get(storageLocation).resolve(filename);
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new MediaFileNotFoundException("El archivo no ha sido encontrado: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new MediaFileNotFoundException("El archivo no ha sido encontrado: " + filename);
        }
    }

    public void delete(String filename) {
        Path path = Paths.get(storageLocation).resolve(filename);
        try {
            FileSystemUtils.deleteRecursively(path);
        } catch (IOException ex) { // lo dejamos pasar } }
        }
    }
}
