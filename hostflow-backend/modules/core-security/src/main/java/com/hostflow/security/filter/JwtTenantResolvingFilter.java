package com.hostflow.security.filter;

import com.hostflow.security.product.ProductContext;
import com.hostflow.security.product.ProductScope;
import com.hostflow.tenancy.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Runs AFTER Spring Security's BearerTokenAuthenticationFilter (registered via
 * addFilterAfter in SecurityConfig), so SecurityContextHolder already has an
 * authenticated JwtAuthenticationToken by the time this filter executes.
 *
 * Extracts tenant_id and product_scope from the verified JWT and populates
 * TenantContext (core-tenancy) and ProductContext (this module) for the
 * duration of the request. THIS is the production replacement for
 * core-tenancy's
 * dev-only TenantHeaderFilter — that filter still exists for local Postman
 * testing
 * without a real Keycloak token, but this filter is what actually runs against
 * verified, signed claims and is safe for staging/production.
 *
 * If a request has no JWT (e.g. it slipped through on a permitAll path), this
 * filter simply does nothing — TenantContext stays unset, and any RLS-protected
 * query downstream fails closed, per core-tenancy's design.
 */
public class JwtTenantResolvingFilter extends OncePerRequestFilter {

    // JwtClaimNames not available; define claim names locally
    // (previously provided by com.hostflow.security.jwt.JwtClaimNames)
    private static final String TENANT_ID_CLAIM = "tenant_id";
    private static final String PRODUCT_SCOPE_CLAIM = "product_scope";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                resolveTenant(jwt);
                resolveProductScope(jwt);
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            ProductContext.clear();
        }
    }

    private void resolveTenant(Jwt jwt) {
        String tenantIdClaim = jwt.getClaimAsString(TENANT_ID_CLAIM);
        if (tenantIdClaim != null && !tenantIdClaim.isBlank()) {
            TenantContext.set(UUID.fromString(tenantIdClaim));
        }
    }

    private void resolveProductScope(Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList(PRODUCT_SCOPE_CLAIM);
        if (scopes == null) {
            return;
        }
        Set<ProductScope> resolved = scopes.stream()
                .map(s -> ProductScope.valueOf(s.toUpperCase()))
                .collect(Collectors.toSet());
        ProductContext.set(resolved);
    }
}
