package pe.todotic.bookstoreapi_s2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig {

    // Con esto nos evitamos hacer por cada controlador el CORS que permita las solicitudes
    @Bean
    public WebMvcConfigurer webMvcConfigurer () {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:4200/") // Permite este origen
                        .allowedMethods("*") // Permite todos los metodos
                        .allowedHeaders("*"); // Permite todos los headers
            }
        };
    }

}
