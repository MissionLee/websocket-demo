package com.example.demowebsocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET","POST","OPTION")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
