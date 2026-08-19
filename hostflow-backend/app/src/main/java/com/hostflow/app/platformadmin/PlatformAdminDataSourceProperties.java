package com.hostflow.app.platformadmin;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Connection details for the hostflow_platform_admin (BYPASSRLS) role, kept
 * entirely separate from the primary hostflow_app DataSource. This is what fixes
 * Open Item A: OverdueInvoiceSweepJob and every cross-tenant admin-screen query
 * now runs against a connection that genuinely bypasses RLS, rather than relying
 * on TenantContext (which is never set for scheduled jobs or platform-wide reads).
 */
@ConfigurationProperties(prefix = "hostflow.datasource.platform-admin")
public class PlatformAdminDataSourceProperties {

    private String url;
    private String username;
    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
