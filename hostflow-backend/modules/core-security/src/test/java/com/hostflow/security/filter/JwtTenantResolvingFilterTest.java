package com.hostflow.security.filter;

import com.hostflow.security.product.ProductContext;
import com.hostflow.tenancy.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTenantResolvingFilterTest {

    private final JwtTenantResolvingFilter filter = new JwtTenantResolvingFilter();

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        TenantContext.clear();
        ProductContext.clear();
    }

    @Test
    void doFilter_populatesTenantAndProductContext_fromJwtClaims_thenClearsAfter() throws Exception {
        UUID tenantId = UUID.randomUUID();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .claim("tenant_id", tenantId.toString())
                .claim("product_scope", List.of("XANUOS"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        JwtAuthenticationToken authToken =
                new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("PRODUCT_XANUOS")));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        boolean[] assertedInsideChain = {false};

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), (req, res) -> {
            assertThat(TenantContext.get()).isEqualTo(tenantId);
            assertThat(ProductContext.hasAccess(com.hostflow.security.product.ProductScope.XANUOS)).isTrue();
            assertedInsideChain[0] = true;
        });

        assertThat(assertedInsideChain[0]).isTrue();
        // Must be cleared after the filter completes, to avoid leaking into the next
        // request handled by the same pooled thread.
        assertThat(TenantContext.isSet()).isFalse();
        assertThat(ProductContext.get()).isEmpty();
    }

    @Test
    void doFilter_withNoAuthentication_doesNothingAndProceeds() throws Exception {
        boolean[] chainCalled = {false};

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(),
                (req, res) -> chainCalled[0] = true);

        assertThat(chainCalled[0]).isTrue();
        assertThat(TenantContext.isSet()).isFalse();
    }
}
