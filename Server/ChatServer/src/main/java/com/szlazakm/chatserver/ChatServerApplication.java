package com.szlazakm.chatserver;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import java.security.Security;
import java.time.Instant;
import java.time.InstantSource;

@SpringBootApplication
@EnableCaching
public class ChatServerApplication {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(ChatServerApplication.class, args);
    }

    @Bean
    public Instant getInstant() {
        return InstantSource.system().instant();
    }
}
