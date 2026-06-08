package app.templebar.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

    private final CorsProperties corsProperties;

    public CorsConfig(
            CorsProperties corsProperties
    ) {
        this.corsProperties = corsProperties;
    }

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowCredentials(true);

        configuration.setAllowedOrigins(
                corsProperties.allowedOrigins()
        );

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return new CorsFilter(source);
    }
}
