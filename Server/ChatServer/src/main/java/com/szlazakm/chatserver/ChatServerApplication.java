package com.szlazakm.chatserver;

import com.szlazakm.chatserver.services.RateLimitingFilter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import java.security.Security;
import java.time.Instant;
import java.time.InstantSource;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class ChatServerApplication {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(ChatServerApplication.class, args);
    }

    @Bean
    public Instant getInstant() {
        return InstantSource.system().instant();
    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration(
            RateLimitingFilter rateLimitingFilter
    ) {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rateLimitingFilter);
        registrationBean.addUrlPatterns("/api/user/keyBundle"); // Register filter for API endpoints
        return registrationBean;
    }
}
