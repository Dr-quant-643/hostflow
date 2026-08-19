package com.hostflow.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "hostflow.rabbitmq")
public class HostFlowRabbitProperties {

    private Exchanges exchanges = new Exchanges();
    private Queues queues = new Queues();
    private String dlq;
    private RoutingKeys routingKeys = new RoutingKeys();

    public Exchanges getExchanges() { return exchanges; }
    public void setExchanges(Exchanges exchanges) { this.exchanges = exchanges; }
    public Queues getQueues() { return queues; }
    public void setQueues(Queues queues) { this.queues = queues; }
    public String getDlq() { return dlq; }
    public void setDlq(String dlq) { this.dlq = dlq; }
    public RoutingKeys getRoutingKeys() { return routingKeys; }
    public void setRoutingKeys(RoutingKeys routingKeys) { this.routingKeys = routingKeys; }

    public static class Exchanges {
        private String direct;
        private String topic;
        private String fanout;
        private String dlx;

        public String getDirect() { return direct; }
        public void setDirect(String direct) { this.direct = direct; }
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public String getFanout() { return fanout; }
        public void setFanout(String fanout) { this.fanout = fanout; }
        public String getDlx() { return dlx; }
        public void setDlx(String dlx) { this.dlx = dlx; }
    }

    /** REMOVED: analytics and ai fields — no longer used. Claude integration
     * (next phase) calls Claude's API directly via synchronous HTTP, not via
     * these queues. */
    public static class Queues {
        private Map<String, String> booking = new HashMap<>();
        private Map<String, String> property = new HashMap<>();
        private Map<String, String> payment = new HashMap<>();
        private Map<String, String> notification = new HashMap<>();
        private Map<String, String> tenant = new HashMap<>();

        public Map<String, String> getBooking() { return booking; }
        public void setBooking(Map<String, String> booking) { this.booking = booking; }
        public Map<String, String> getProperty() { return property; }
        public void setProperty(Map<String, String> property) { this.property = property; }
        public Map<String, String> getPayment() { return payment; }
        public void setPayment(Map<String, String> payment) { this.payment = payment; }
        public Map<String, String> getNotification() { return notification; }
        public void setNotification(Map<String, String> notification) { this.notification = notification; }
        public Map<String, String> getTenant() { return tenant; }
        public void setTenant(Map<String, String> tenant) { this.tenant = tenant; }
    }

    public static class RoutingKeys {
        private Map<String, String> booking = new HashMap<>();
        private Map<String, String> property = new HashMap<>();
        private Map<String, String> payment = new HashMap<>();
        private Map<String, String> notification = new HashMap<>();
        private Map<String, String> tenant = new HashMap<>();

        public Map<String, String> getBooking() { return booking; }
        public void setBooking(Map<String, String> booking) { this.booking = booking; }
        public Map<String, String> getProperty() { return property; }
        public void setProperty(Map<String, String> property) { this.property = property; }
        public Map<String, String> getPayment() { return payment; }
        public void setPayment(Map<String, String> payment) { this.payment = payment; }
        public Map<String, String> getNotification() { return notification; }
        public void setNotification(Map<String, String> notification) { this.notification = notification; }
        public Map<String, String> getTenant() { return tenant; }
        public void setTenant(Map<String, String> tenant) { this.tenant = tenant; }
    }
}
