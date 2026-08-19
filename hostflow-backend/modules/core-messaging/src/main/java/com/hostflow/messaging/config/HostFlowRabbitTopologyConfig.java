package com.hostflow.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(HostFlowRabbitProperties.class)
public class HostFlowRabbitTopologyConfig {

    private final HostFlowRabbitProperties properties;

    public HostFlowRabbitTopologyConfig(HostFlowRabbitProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(properties.getExchanges().getDirect());
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(properties.getExchanges().getTopic());
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(properties.getExchanges().getFanout());
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(properties.getExchanges().getDlx());
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(properties.getDlq()).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, @Qualifier("deadLetterExchange") DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dead");
    }

    // Declarables (not a plain List<Queue>) — RabbitAdmin's auto-declaration
    // scan only recognizes individual Declarable beans or a Declarables
    // wrapper; a raw List<Queue> bean is invisible to it, so none of these
    // queues were ever actually created on the broker and every
    // @RabbitListener consuming one failed its passive declare check with
    // 404 NOT_FOUND at startup.
    @Bean
    public Declarables domainQueues() {
        List<Queue> queues = new ArrayList<>();
        queues.addAll(buildDomainQueues(properties.getQueues().getBooking()));
        queues.addAll(buildDomainQueues(properties.getQueues().getProperty()));
        queues.addAll(buildDomainQueues(properties.getQueues().getPayment()));
        queues.addAll(buildDomainQueues(properties.getQueues().getNotification()));
        queues.addAll(buildDomainQueues(properties.getQueues().getTenant()));
        return new Declarables(new ArrayList<Declarable>(queues));
    }

    private List<Queue> buildDomainQueues(Map<String, String> queueNamesByEvent) {
        List<Queue> result = new ArrayList<>();
        for (String queueName : queueNamesByEvent.values()) {
            result.add(QueueBuilder.durable(queueName)
                    .withArgument("x-dead-letter-exchange", properties.getExchanges().getDlx())
                    .withArgument("x-dead-letter-routing-key", "dead")
                    .build());
        }
        return result;
    }

    @Bean
    public Declarables domainBindings(Declarables domainQueues, @Qualifier("directExchange") DirectExchange directExchange, TopicExchange topicExchange) {
        List<Binding> bindings = new ArrayList<>();
        bindings.addAll(bindDomain(directExchange, topicExchange, properties.getQueues().getBooking(), properties.getRoutingKeys().getBooking()));
        bindings.addAll(bindDomain(directExchange, topicExchange, properties.getQueues().getProperty(), properties.getRoutingKeys().getProperty()));
        bindings.addAll(bindDomain(directExchange, topicExchange, properties.getQueues().getPayment(), properties.getRoutingKeys().getPayment()));
        bindings.addAll(bindDomain(directExchange, topicExchange, properties.getQueues().getNotification(), properties.getRoutingKeys().getNotification()));
        bindings.addAll(bindDomain(directExchange, topicExchange, properties.getQueues().getTenant(), properties.getRoutingKeys().getTenant()));
        return new Declarables(new ArrayList<Declarable>(bindings));
    }

    private List<Binding> bindDomain(DirectExchange directExchange, TopicExchange topicExchange,
                                      Map<String, String> queueNamesByEvent, Map<String, String> routingKeysByEvent) {
        List<Binding> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : queueNamesByEvent.entrySet()) {
            String routingKey = routingKeysByEvent.get(entry.getKey());
            if (routingKey == null) continue;
            Queue queue = QueueBuilder.durable(entry.getValue()).build();
            if (routingKey.contains("*") || routingKey.contains("#")) {
                result.add(BindingBuilder.bind(queue).to(topicExchange).with(routingKey));
            } else {
                result.add(BindingBuilder.bind(queue).to(directExchange).with(routingKey));
            }
        }
        return result;
    }
}
