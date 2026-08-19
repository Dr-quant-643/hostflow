package com.hostflow.gateway.health;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hostflow.gateway.health")
public class AdminHealthProperties {

    private String appServiceUri;
    private int timeoutSeconds = 5;

    public String getAppServiceUri() {
        return appServiceUri;
    }

    public void setAppServiceUri(String appServiceUri) {
        this.appServiceUri = appServiceUri;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
