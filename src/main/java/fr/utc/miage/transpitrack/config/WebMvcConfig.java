package fr.utc.miage.transpitrack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC configuration that maps the {@code /images/users/**} URL prefix to the
 * filesystem directory where user profile images are stored.
 * <p>
 * The upload directory is injected from the {@code app.upload.users-images-dir}
 * application property.
 * </p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** Filesystem path to the directory that holds uploaded user profile images. */
    @Value("${app.upload.users-images-dir}")
    private String uploadDir;

    /**
     * Registers a resource handler so that requests to {@code /images/users/**}
     * are served from the configured upload directory on disk.
     *
     * @param registry the Spring MVC resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/users/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
