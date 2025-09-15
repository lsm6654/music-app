package com.example.music.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.data.redis.port:6379}")
  private int redisPort;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisHost, redisPort);
  }

  @Bean
  public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(LettuceConnectionFactory connectionFactory) {
    StringRedisSerializer serializer = new StringRedisSerializer();

    RedisSerializationContext<String, String> serializationContext =
      RedisSerializationContext.<String, String>newSerializationContext()
        .key(serializer)
        .value(serializer)
        .hashKey(serializer)
        .hashValue(serializer)
        .build();

    return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
  }

  @Configuration
  @Profile("!prod")
  public static class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
      try {
        redisServer = RedisServer.newRedisServer()
          .port(redisPort)
          .setting("maxmemory 128M")
          .build();
        
        redisServer.start();
        log.info("Embedded Redis started on port {}", redisPort);
      } catch (Exception e) {
        log.error("Failed to start embedded Redis", e);
      }
    }

    @PreDestroy
    public void stopRedis() {
      if (redisServer != null && redisServer.isActive()) {
        try {
          redisServer.stop();
          log.info("Embedded Redis stopped");
        } catch (IOException e) {
          log.error("Failed to stop embedded Redis", e);
        }
      }
    }
  }

}
