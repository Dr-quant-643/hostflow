package com.hostflow.tenancy.web;

import com.hostflow.tenancy.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * TEMPORARY, DEV/TEST ONLY. Reads tenant id from a raw X-Tenant-Id header with zero
 * verification. Exists purely so module-property, module-booking, etc. can be built
 * and tested via Postman/curl before Keycloak exists.
 *
 * MUST be replaced in core-security (module 5) by a filter that resolves tenant_id
 * from a verified Keycloak JWT claim. This class must never be active in staging or
 * production — enforced via @Profile({"dev","test"}) in TenantFilterConfig, not by
 * convention alone.
 */
public class TenantHeaderFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String header = request.getHeader(TENANT_HEADER);
            if (header != null && !header.isBlank()) {
                TenantContext.set(UUID.fromString(header));
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
