package com.hostflow.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;

// redisRateLimiter lives in config.RouteConfig and tenantKeyResolver lives in
// ratelimit.TenantKeyResolver (a @Component) — both used to be duplicated
// here too, which only surfaced as a "bean already defined" startup failure
// the first time this service was actually run end-to-end.
//
// Neither Redis auto-configuration can be excluded here, even though only
// the reactive side is used directly: RedisReactiveAutoConfiguration's
// template beans are @ConditionalOnBean(ReactiveRedisConnectionFactory.class),
// and that connection factory bean is actually produced by
// RedisAutoConfiguration's shared Lettuce connection configuration, not by
// RedisReactiveAutoConfiguration itself. Excluding RedisAutoConfiguration
// silently starved the reactive template of the connection factory it
// needs — another gap only visible once this service actually tried to
// build its routes.
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    FlywayAutoConfiguration.class,
    RabbitAutoConfiguration.class
})
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
