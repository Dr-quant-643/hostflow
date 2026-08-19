package com.hostflow.security.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductAccessGuardTest {

    private final ProductAccessGuard productAccessGuard = new ProductAccessGuard();

    @BeforeEach
    void setUp() {
        ProductContext.clear();
    }

    @AfterEach
    void tearDown() {
        ProductContext.clear();
    }

    // ==================== assertAccess Tests ====================

    @Test
    void assertAccess_throws_whenScopeMissing() {
        ProductScope requiredScope = ProductScope.XANUOS;

        assertThatThrownBy(() -> productAccessGuard.assertAccess(requiredScope))
                .isInstanceOf(ProductAccessDeniedException.class)
                .hasMessage("This action requires access to XANUOS, which was not found in the current token");
    }

    @Test
    void assertAccess_doesNotThrow_whenScopeMatches() {
        ProductContext.set(ProductScope.XANUOS);
        productAccessGuard.assertAccess(ProductScope.XANUOS);
    }

    @Test
    void assertAccess_doesNotThrow_whenPlatformAdmin() {
        ProductContext.set(ProductScope.PLATFORM);
        productAccessGuard.assertAccess(ProductScope.XANUOS);
    }

    @Test
    void assertAccess_withString_throws_whenScopeMissing() {
        String requiredScope = "XANUOS";

        assertThatThrownBy(() -> productAccessGuard.assertAccess(requiredScope))
                .isInstanceOf(ProductAccessDeniedException.class)
                .hasMessage("This action requires access to XANUOS, which was not found in the current token");
    }

    @Test
    void assertAccess_withString_doesNotThrow_whenScopeMatches() {
        ProductContext.set(ProductScope.XANUOS);
        productAccessGuard.assertAccess("XANUOS");
    }

    @Test
    void assertAccess_withString_throws_whenInvalidScope() {
        String requiredScope = "INVALID_SCOPE";

        assertThatThrownBy(() -> productAccessGuard.assertAccess(requiredScope))
                .isInstanceOf(ProductAccessDeniedException.class)
                .hasMessage("Invalid product scope: INVALID_SCOPE");
    }

    @Test
    void assertAccess_withNull_doesNotThrow() {
        productAccessGuard.assertAccess((ProductScope) null);
        productAccessGuard.assertAccess((String) null);
    }

    @Test
    void assertAccess_withEmptyString_doesNotThrow() {
        productAccessGuard.assertAccess("");
    }

    // ==================== assertAnyAccess Tests ====================

    @Test
    void assertAnyAccess_throws_whenNoScopesMatch() {
        ProductContext.set(ProductScope.XANUOS);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.NAZILCO);

        assertThatThrownBy(() -> productAccessGuard.assertAnyAccess(requiredScopes))
                .isInstanceOf(ProductAccessDeniedException.class)
                .hasMessage("This action requires access to one of: NAZILCO, which was not found in the current token");
    }

    @Test
    void assertAnyAccess_doesNotThrow_whenOneScopeMatches() {
        ProductContext.set(ProductScope.XANUOS);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        productAccessGuard.assertAnyAccess(requiredScopes);
    }

    @Test
    void assertAnyAccess_doesNotThrow_whenPlatformAdmin() {
        ProductContext.set(ProductScope.PLATFORM);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        productAccessGuard.assertAnyAccess(requiredScopes);
    }

    @Test
    void assertAnyAccess_withNull_doesNotThrow() {
        productAccessGuard.assertAnyAccess(null);
        productAccessGuard.assertAnyAccess(Set.of());
    }

    // ==================== assertAllAccess Tests ====================

    @Test
    void assertAllAccess_throws_whenNotAllScopesMatch() {
        ProductContext.set(ProductScope.XANUOS);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);

        assertThatThrownBy(() -> productAccessGuard.assertAllAccess(requiredScopes))
                .isInstanceOf(ProductAccessDeniedException.class)
                .hasMessage("This action requires all of: NAZILCO, XANUOS");
    }

    @Test
    void assertAllAccess_doesNotThrow_whenAllScopesMatch() {
        ProductContext.set(Set.of(ProductScope.XANUOS, ProductScope.NAZILCO));
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        productAccessGuard.assertAllAccess(requiredScopes);
    }

    @Test
    void assertAllAccess_doesNotThrow_whenPlatformAdmin() {
        ProductContext.set(ProductScope.PLATFORM);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        productAccessGuard.assertAllAccess(requiredScopes);
    }

    @Test
    void assertAllAccess_withNull_doesNotThrow() {
        productAccessGuard.assertAllAccess(null);
        productAccessGuard.assertAllAccess(Set.of());
    }

    // ==================== hasAccess Tests ====================

    @Test
    void hasAccess_returnsFalse_whenScopeMissing() {
        ProductContext.set(ProductScope.XANUOS);
        assertThat(productAccessGuard.hasAccess(ProductScope.NAZILCO)).isFalse();
    }

    @Test
    void hasAccess_returnsTrue_whenScopeMatches() {
        ProductContext.set(ProductScope.XANUOS);
        assertThat(productAccessGuard.hasAccess(ProductScope.XANUOS)).isTrue();
    }

    @Test
    void hasAccess_returnsTrue_whenPlatformAdmin() {
        ProductContext.set(ProductScope.PLATFORM);
        assertThat(productAccessGuard.hasAccess(ProductScope.XANUOS)).isTrue();
        assertThat(productAccessGuard.hasAccess(ProductScope.NAZILCO)).isTrue();
    }

    @Test
    void hasAccess_withString_returnsFalse_whenScopeMissing() {
        ProductContext.set(ProductScope.XANUOS);
        assertThat(productAccessGuard.hasAccess("NAZILCO")).isFalse();
    }

    @Test
    void hasAccess_withString_returnsTrue_whenScopeMatches() {
        ProductContext.set(ProductScope.XANUOS);
        assertThat(productAccessGuard.hasAccess("XANUOS")).isTrue();
    }

    @Test
    void hasAccess_withString_returnsFalse_whenInvalidScope() {
        ProductContext.set(ProductScope.XANUOS);
        assertThat(productAccessGuard.hasAccess("INVALID_SCOPE")).isFalse();
    }

    // ==================== hasAnyAccess Tests ====================

    @Test
    void hasAnyAccess_returnsFalse_whenNoScopesMatch() {
        ProductContext.set(ProductScope.XANUOS);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.NAZILCO);
        assertThat(productAccessGuard.hasAnyAccess(requiredScopes)).isFalse();
    }

    @Test
    void hasAnyAccess_returnsTrue_whenOneScopeMatches() {
        ProductContext.set(ProductScope.XANUOS);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        assertThat(productAccessGuard.hasAnyAccess(requiredScopes)).isTrue();
    }

    @Test
    void hasAnyAccess_returnsTrue_whenPlatformAdmin() {
        ProductContext.set(ProductScope.PLATFORM);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        assertThat(productAccessGuard.hasAnyAccess(requiredScopes)).isTrue();
    }

    // ==================== hasAllAccess Tests ====================

    @Test
    void hasAllAccess_returnsFalse_whenNotAllScopesMatch() {
        ProductContext.set(ProductScope.XANUOS);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        assertThat(productAccessGuard.hasAllAccess(requiredScopes)).isFalse();
    }

    @Test
    void hasAllAccess_returnsTrue_whenAllScopesMatch() {
        ProductContext.set(Set.of(ProductScope.XANUOS, ProductScope.NAZILCO));
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        assertThat(productAccessGuard.hasAllAccess(requiredScopes)).isTrue();
    }

    @Test
    void hasAllAccess_returnsTrue_whenPlatformAdmin() {
        ProductContext.set(ProductScope.PLATFORM);
        Set<ProductScope> requiredScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        assertThat(productAccessGuard.hasAllAccess(requiredScopes)).isTrue();
    }

    // ==================== getCurrentUserScope Tests ====================

    @Test
    void getCurrentUserScope_returnsFirstScope() {
        ProductContext.set(Set.of(ProductScope.XANUOS, ProductScope.NAZILCO));
        ProductScope result = productAccessGuard.getCurrentUserScope();
        assertThat(result).isEqualTo(ProductScope.XANUOS);
    }

    @Test
    void getCurrentUserScope_returnsPlatform_whenPresent() {
        ProductContext.set(Set.of(ProductScope.NAZILCO, ProductScope.PLATFORM));
        ProductScope result = productAccessGuard.getCurrentUserScope();
        assertThat(result).isEqualTo(ProductScope.PLATFORM);
    }

    @Test
    void getCurrentUserScope_returnsNull_whenNoScopes() {
        ProductScope result = productAccessGuard.getCurrentUserScope();
        assertThat(result).isNull();
    }

    // ==================== getCurrentUserScopes Tests ====================

    @Test
    void getCurrentUserScopes_returnsAllScopes() {
        Set<ProductScope> expectedScopes = Set.of(ProductScope.XANUOS, ProductScope.NAZILCO);
        ProductContext.set(expectedScopes);
        Set<ProductScope> result = productAccessGuard.getCurrentUserScopes();
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedScopes);
    }

    @Test
    void getCurrentUserScopes_returnsEmptySet_whenNoScopes() {
        Set<ProductScope> result = productAccessGuard.getCurrentUserScopes();
        assertThat(result).isEmpty();
    }

    // ==================== isPlatformAdmin Tests ====================

    @Test
    void isPlatformAdmin_returnsTrue_whenPlatformScopePresent() {
        ProductContext.set(ProductScope.PLATFORM);
        assertThat(productAccessGuard.isPlatformAdmin()).isTrue();
    }

    @Test
    void isPlatformAdmin_returnsFalse_whenNoPlatformScope() {
        ProductContext.set(ProductScope.XANUOS);
        assertThat(productAccessGuard.isPlatformAdmin()).isFalse();
    }

    @Test
    void isPlatformAdmin_returnsFalse_whenNoScopes() {
        assertThat(productAccessGuard.isPlatformAdmin()).isFalse();
    }

    // ==================== Multiple Scope Tests ====================

    @Test
    void assertAccess_withMultipleScopes_allowsAccessToAny() {
        ProductContext.set(Set.of(ProductScope.XANUOS, ProductScope.NAZILCO));

        productAccessGuard.assertAccess(ProductScope.XANUOS);
        productAccessGuard.assertAccess(ProductScope.NAZILCO);
        assertThatThrownBy(() -> productAccessGuard.assertAccess(ProductScope.PLATFORM))
                .isInstanceOf(ProductAccessDeniedException.class);
    }

    // ==================== ProductContext Direct Tests ====================

    @Test
    void productContext_getPrimary_returnsCorrectScope() {
        // Given
        ProductContext.set(Set.of(ProductScope.XANUOS, ProductScope.NAZILCO));

        // When / Then
        assertThat(ProductContext.getPrimary()).isEqualTo(ProductScope.XANUOS);

        // When platform is present
        ProductContext.set(Set.of(ProductScope.NAZILCO, ProductScope.PLATFORM));
        assertThat(ProductContext.getPrimary()).isEqualTo(ProductScope.PLATFORM);

        // When no scopes
        ProductContext.clear();
        assertThat(ProductContext.getPrimary()).isNull();
    }

    @Test
    void productContext_requireAccess_throwsCorrectException() {
        ProductContext.set(ProductScope.XANUOS);

        assertThatThrownBy(() -> ProductContext.requireAccess(ProductScope.NAZILCO))
                .isInstanceOf(ProductAccessDeniedException.class)
                .hasMessage("This action requires access to NAZILCO, which was not found in the current token");
    }

    @Test
    void productContext_requireAnyAccess_throwsCorrectException() {
        ProductContext.set(ProductScope.XANUOS);

        assertThatThrownBy(() -> ProductContext.requireAnyAccess(Set.of(ProductScope.NAZILCO)))
                .isInstanceOf(ProductAccessDeniedException.class)
                .hasMessage("This action requires access to one of: NAZILCO, which was not found in the current token");
    }
}
