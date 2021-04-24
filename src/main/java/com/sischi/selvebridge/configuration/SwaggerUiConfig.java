package com.sischi.selvebridge.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerUiConfig {

    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvidfer() {
        return new SwaggerResourcesProvider() {

            @Override
            public List<SwaggerResource> get() {
                List<SwaggerResource> resources = new ArrayList<>();

                SwaggerResource resource = new SwaggerResource();
                resource.setSwaggerVersion("2.0");
                resource.setName("selve-bridge");
                resource.setLocation("/selvebridge-api.yml");

                resources.add(resource);
                return resources;
            }
        };
    }

}
