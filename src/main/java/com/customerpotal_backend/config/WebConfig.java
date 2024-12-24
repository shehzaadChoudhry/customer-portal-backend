package com.customerpotal_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	public void addCorsMapping(CorsRegistry registry) {
	registry.addMapping("/**")
	.allowedOrigins("http://localhost:3000")
	.allowedMethods("GET", "POST", "PUT", "DELETE")
	.allowedHeaders("")
	.allowCredentials(true);
	}
}
