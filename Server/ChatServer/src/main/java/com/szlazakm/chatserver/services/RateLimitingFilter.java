package com.szlazakm.chatserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.whispersystems.libsignal.logging.Log;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitingFilter implements Filter {

    // Map to store request counts per IP address
    private Map<String, AtomicInteger> requestCountsPerIpAddress = new ConcurrentHashMap<>();

    // Maximum requests allowed per minute
    private static final int MAX_REQUESTS_PER_MINUTE = 10;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String clientIpAddress = httpServletRequest.getRemoteAddr();

        // Initialize request count for the client IP address
        requestCountsPerIpAddress.putIfAbsent(clientIpAddress, new AtomicInteger(0));
        AtomicInteger requestCount = requestCountsPerIpAddress.get(clientIpAddress);

        log.debug("Requests for " + clientIpAddress + " : " + requestCount.toString());

        // Increment the request count
        int requests = requestCount.incrementAndGet();

        // Check if the request limit has been exceeded
        if (requests > MAX_REQUESTS_PER_MINUTE) {
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");

            // Create a response payload similar to ResponseEntity
            String jsonResponse = String.format(
                    "{\"status\":%d,\"error\":\"Too Many Requests\",\"message\":\"Too many requests. Please try again later.\"}",
                    HttpStatus.TOO_MANY_REQUESTS.value()
            );

            httpServletResponse.getWriter().write(jsonResponse);
            httpServletResponse.getWriter().flush();
            return;
        }

        // Allow the request to proceed
        chain.doFilter(request, response);
    }

    @Scheduled(
            fixedRate = 20,
            timeUnit = TimeUnit.SECONDS
    )
    public void clearCache() {
        log.debug("RateLimitingFilter::clearCache");
        requestCountsPerIpAddress = new ConcurrentHashMap<>();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional: Initialization logic, if needed
    }

    @Override
    public void destroy() {
        // Optional: Cleanup resources, if needed
    }
}
