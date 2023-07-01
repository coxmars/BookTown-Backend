package pe.todotic.bookstoreapi_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.todotic.bookstoreapi_s2.service.StorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    Map<String,String> upload (@RequestParam(name = "filename")MultipartFile multipartFile) {
        String path = storageService.store(multipartFile);
        return Map.of("path",path);
    }

    @GetMapping("/{filename}")
    ResponseEntity<Resource> getResource (@PathVariable String filename) throws IOException {
        Resource resource = storageService.loadAsResource(filename);
        String contentType = Files.probeContentType(resource.getFile().toPath());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,contentType).body(resource);
    }

}
