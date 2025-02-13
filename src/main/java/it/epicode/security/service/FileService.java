package it.epicode.security.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileService {

    private static final String UPLOAD_DIR = "uploads/";

    public FileService() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Errore nella creazione della cartella uploads", e);
        }
    }

    public String saveImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                System.err.println("❌ File vuoto!");
                throw new RuntimeException("File vuoto!");
            }

            System.out.println("✅ Nome del file ricevuto: " + file.getOriginalFilename());
            System.out.println("✅ Tipo di file ricevuto: " + file.getContentType());

            // Controlla se il file è un'immagine
            if (!file.getContentType().startsWith("image/")) {
                System.err.println("❌ Il file non è un'immagine valida!");
                throw new RuntimeException("Il file non è un'immagine valida!");
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path destinationPath = Paths.get(UPLOAD_DIR).resolve(fileName);

            System.out.println("✅ Percorso di destinazione: " + destinationPath.toString());

            // Salva il file nel percorso specificato
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✅ File salvato con successo: " + destinationPath.toString());
            return fileName; // Restituisci solo il nome del file
        } catch (IOException e) {
            System.err.println("❌ Errore durante il salvataggio del file: " + e.getMessage());
            throw new RuntimeException("Errore nel salvataggio del file.", e);
        }
    }


    public ResponseEntity<Resource> serveFile(String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
