package com.saea.itasset.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 프론트엔드 연동을 위한 CORS 설정 (운영 도메인 포함)
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:5173", 
                    "http://127.0.0.1:5173", 
                    "https://localhost:5173",
                    "https://itasset.sae-a.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 인증 인터셉터 적용 (로그인, 검색 등의 일부 public API 제외)
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/users/login", "/api/users/logout");
    }
}
