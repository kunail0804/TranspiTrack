package fr.utc.miage.transpitrack.Model.Jpa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageStorageServiceTest {

    @TempDir
    Path tempDir;

    private ImageStorageService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new ImageStorageService();
        setUploadDir(service, tempDir.toString());
        service.init();
        Files.write(tempDir.resolve("placeholder.png"), "placeholder".getBytes());
    }

    private void setUploadDir(ImageStorageService svc, String path) throws Exception {
        Field field = ImageStorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(svc, path);
    }

    private MultipartFile mockFile(String originalName, byte[] content) throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(originalName);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        return file;
    }

    // ──────────────────────────────────────────────────────────────
    // init()
    // ──────────────────────────────────────────────────────────────
    @Test
    void initShouldCreateUploadDirectoryWhenItDoesNotExist() throws Exception {
        Path subDir = tempDir.resolve("newSubDir");
        ImageStorageService freshService = new ImageStorageService();
        setUploadDir(freshService, subDir.toString());

        freshService.init();

        assertTrue(Files.isDirectory(subDir));
    }

    @Test
    void initShouldNotThrowWhenDirectoryAlreadyExists() throws IOException {
        service.init();

        assertTrue(Files.isDirectory(tempDir));
    }

    // ──────────────────────────────────────────────────────────────
    // store()
    // ──────────────────────────────────────────────────────────────
    @Test
    void storeShouldReturnNullWhenFileIsNull() throws IOException {
        String result = service.store(null);

        assertNull(result);
    }

    @Test
    void storeShouldReturnNullWhenFileIsEmpty() throws IOException {
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        String result = service.store(emptyFile);

        assertNull(result);
    }

    @Test
    void storeShouldHandleNullOriginalFilename() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String result = service.store(file);

        assertNotNull(result);
        assertFalse(result.contains(".")); // no extension
    }

    @Test
    void storeShouldReturnNonNullFilenameWhenFileIsValid() throws IOException {
        MultipartFile file = mockFile("photo.png", "data".getBytes());

        String result = service.store(file);

        assertNotNull(result);
    }

    @Test
    void storeShouldPreserveFileExtension() throws IOException {
        MultipartFile file = mockFile("avatar.jpg", "data".getBytes());

        String result = service.store(file);

        assertNotNull(result);
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    void storeShouldHandleFilenameWithoutExtension() throws IOException {
        MultipartFile file = mockFile("avatar", "data".getBytes());

        String result = service.store(file);

        assertNotNull(result);
        assertFalse(result.contains("."));
    }

    @Test
    void storeShouldWriteFileContentToDisk() throws IOException {
        byte[] content = "image-bytes".getBytes();
        MultipartFile file = mockFile("photo.png", content);

        String filename = service.store(file);

        byte[] written = Files.readAllBytes(tempDir.resolve(filename));
        assertEquals(new String(content), new String(written));
    }

    @Test
    void storeShouldGenerateUniqueFilenamesForConsecutiveUploads() throws IOException {
        MultipartFile file1 = mockFile("a.png", "data1".getBytes());
        MultipartFile file2 = mockFile("b.png", "data2".getBytes());

        String name1 = service.store(file1);
        String name2 = service.store(file2);

        assertNotEquals(name1, name2);
    }

    // ──────────────────────────────────────────────────────────────
    // delete()
    // ──────────────────────────────────────────────────────────────
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   ", "placeholder.png"})
    void deleteShouldDoNothingForProtectedValues(String filename) {
        service.delete(filename);

        assertTrue(Files.isDirectory(tempDir));
        assertTrue(Files.exists(tempDir.resolve("placeholder.png")));
    }

    @Test
    void deleteShouldRemoveExistingFileFromDisk() throws IOException {
        Path target = tempDir.resolve("toDelete.png");
        Files.write(target, "content".getBytes());

        service.delete("toDelete.png");

        assertFalse(Files.exists(target));
    }

    @Test
    void deleteShouldNotThrowWhenFileDoesNotExist() {
        service.delete("nonexistent.png");

        assertTrue(Files.isDirectory(tempDir));
    }

    @Test
    void deleteShouldSilentlyHandleIOExceptionFromFileSystem() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(any(Path.class)))
                    .thenThrow(new IOException("disk error"));

            assertDoesNotThrow(() -> service.delete("someFile.png"));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // getPlaceholderFilename()
    // ──────────────────────────────────────────────────────────────
    @Test
    void getPlaceholderFilenameShouldReturnPlaceholderPng() {
        assertEquals("placeholder.png", service.getPlaceholderFilename());
    }
}
