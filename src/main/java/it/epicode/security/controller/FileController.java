package it.epicode.security.controller;

import it.epicode.security.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/uploads")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // 📌 Carica un'immagine e restituisce il nome del file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Ricevuto file: " + file.getOriginalFilename());
            String filePath = fileService.saveImage(file);
            System.out.println("✅ Immagine caricata con successo. Percorso: " + filePath);

            // Restituisci la risposta come JSON
            return ResponseEntity.ok("{\"imageUrl\": \"/uploads/" + filePath + "\"}");
        } catch (Exception e) {
            System.err.println("❌ Errore nel caricamento del file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore nel caricamento dell'immagine");
        }
    }


    // 📌 Serve le immagini salvate (Es: http://localhost:8080/uploads/nome-file.jpg)
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        return fileService.serveFile(filename); // ✅ Usa direttamente la ResponseEntity
    }

}
