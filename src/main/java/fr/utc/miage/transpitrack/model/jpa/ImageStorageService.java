package fr.utc.miage.transpitrack.model.jpa;

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

/**
 * Service responsible for storing and deleting user profile images on the filesystem.
 * <p>
 * Images are saved under the directory configured by {@code app.upload.users-images-dir}
 * with a UUID-based filename to avoid collisions. A placeholder image
 * ({@value #PLACEHOLDER_FILENAME}) is never deleted by this service.
 * </p>
 */
@Service
public class ImageStorageService {

    /** Filename of the default placeholder profile image that is never deleted. */
    private static final String PLACEHOLDER_FILENAME = "placeholder.png";

    /** Filesystem path to the upload directory, injected from application properties. */
    @Value("${app.upload.users-images-dir}")
    private String uploadDir;

    /** Resolved {@link Path} to the upload directory, initialised on startup. */
    private Path uploadPath;

    /** No-arg constructor; Spring manages instantiation and dependency injection. */
    public ImageStorageService() {
        // Spring-managed bean.
    }

    /**
     * Initialises the upload directory on application startup, creating it if absent.
     *
     * @throws IOException if the directory cannot be created
     */
    @PostConstruct
    public void init() throws IOException {
        uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
    }

    /**
     * Stores the given file in the upload directory under a UUID-based filename.
     * Returns {@code null} without writing anything if the file is absent or empty.
     *
     * @param file the multipart file to store, may be {@code null} or empty
     * @return the generated filename (without path), or {@code null} if no file was stored
     * @throws IOException if the file cannot be written to disk
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
        Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    /**
     * Deletes the file with the given name from the upload directory.
     * Does nothing if {@code filename} is {@code null}, blank, or equals
     * the placeholder filename ({@value #PLACEHOLDER_FILENAME}).
     * Deletion failures are silently ignored.
     *
     * @param filename the filename to delete, relative to the upload directory
     */
    public void delete(String filename) {
        if (filename == null || filename.isBlank() || filename.equals(PLACEHOLDER_FILENAME)) {
            return;
        }
        try {
            Files.deleteIfExists(uploadPath.resolve(filename));
        } catch (IOException _) {
            // log silently
        }
    }

    /**
     * Returns the filename of the default placeholder profile image.
     *
     * @return the placeholder filename ({@value #PLACEHOLDER_FILENAME})
     */
    public String getPlaceholderFilename() {
        return PLACEHOLDER_FILENAME;
    }
}
