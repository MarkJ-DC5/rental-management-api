package com.rental.rental_management_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.TreeMap;

import static java.util.Collections.addAll;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Rental Management System REST API")
                                            .summary("REST API for managing apartment buildings, tenants, and payments.")
                                            .contact(new Contact().name("Mark Jayson Dela Cruz")
                                                                  .email("markjaysondc5@gmail.com"))
                                            .version("1.0"))
                            .servers(List.of(new Server().description("Local")
                                                         .url("http://localhost:8080")));
    }

    @Bean
    public OpenApiCustomizer tagOrderCustomiser() {
        return openApi -> {
            List<Tag> orderedTags = List.of(
                new Tag().name("1. Building").description("Endpoints for building management"),
                new Tag().name("2. Room").description("Endpoints for room management"),
                new Tag().name("3. Tenant").description("Endpoints for tenant management"),
                new Tag().name("4. Transaction").description("Endpoints for transaction management"),
                new Tag().name("5. Authentication").description("Endpoints for user management")
                                           );
            openApi.setTags(orderedTags);
        };
    }

    @Bean
    public OpenApiCustomizer sortPathsByMethod() {
        return openApi -> {
            TreeMap<String, PathItem> sortedPaths = new TreeMap<>();
            openApi.getPaths().forEach((path, pathItem) -> {
                PathItem newItem = new PathItem();
                if (pathItem.getGet() != null) newItem.setGet(pathItem.getGet());
                if (pathItem.getPost() != null) newItem.setPost(pathItem.getPost());
                if (pathItem.getPut() != null) newItem.setPut(pathItem.getPut());
                if (pathItem.getDelete() != null) newItem.setDelete(pathItem.getDelete());
                if (pathItem.getPatch() != null) newItem.setPatch(pathItem.getPatch());
                sortedPaths.put(path, newItem);
            });

            Paths newPaths = new Paths();
            sortedPaths.forEach(newPaths::addPathItem);
            openApi.setPaths(newPaths);
        };
    }
}
