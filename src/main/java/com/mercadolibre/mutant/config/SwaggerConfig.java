package com.mercadolibre.mutant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DNA Mutant Analyzer API")
                        .version("1.0.0")
                        .description("Sistema avanzado de análisis genético para identificación de mutantes. "
                                + "Detecta patrones específicos en secuencias de ADN mediante algoritmos optimizados."));
    }
}
