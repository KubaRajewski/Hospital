package com.example.zajecia7doktorki.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Brajan",
                        email = "brajan.m.szymanski@gmail.com",
                        url = "https://github.com/10brajan"
                ),
                description = "Hospital management application",
                title = "Hospital Management Rest API",
                version = "v1.0"
        )
)
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("rest")
                .pathsToMatch("/**")
                .addOpenApiMethodFilter(method -> {
                    Class<?> declaringClass = method.getDeclaringClass();
                    return declaringClass.isAnnotationPresent(RestController.class);
                })
                .build();
    }
}
