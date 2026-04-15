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
        Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public void delete(String filename) {
        if (filename == null || filename.isBlank() || filename.equals(PLACEHOLDER_FILENAME)) {
            return;
        }
        try {
            Files.deleteIfExists(uploadPath.resolve(filename));
        } catch (IOException e) {
            // log silently
        }
    }

    public String getPlaceholderFilename() {
        return PLACEHOLDER_FILENAME;
    }
}
