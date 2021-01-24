package com.ionos.domains.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfiguration {

    @Bean
    public Jedis jedis() {
        return new Jedis();
    }
}