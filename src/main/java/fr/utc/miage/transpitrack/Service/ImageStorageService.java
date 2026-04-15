package fr.utc.miage.transpitrack.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class ImageStorageService {

    private static final String PLACEHOLDER_FILENAME = "placeholder.png";

    @Value("${app.upload.users-images-dir}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() throws IOException {
        uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
    }

    /**
     * Saves the uploaded file and returns the generated filename.
     * Returns null if the file is empty.
     */
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + extension;
        Path destination = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    /**
     * Deletes the file with the given filename from the upload directory.
     * Does nothing if filename is null, blank, or the placeholder.
     */
    public void delete(String filename) {
        if (filename == null || filename.isBlank() || filename.equals(PLACEHOLDER_FILENAME)) {
            return;
        }
        try {
            Path target = uploadPath.resolve(filename);
            Files.deleteIfExists(target);
        } catch (IOException e) {
            // log silently — file may already be gone
        }
    }

    public String getPlaceholderFilename() {
        return PLACEHOLDER_FILENAME;
    }
}
