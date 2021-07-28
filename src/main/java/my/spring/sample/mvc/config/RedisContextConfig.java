package my.spring.sample.mvc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.validation.constraints.NotNull;

@Slf4j
@Configuration
public class RedisContextConfig {

    @NotNull
    @Value("${resource.redis.hostname}")
    private String hostname;

    @NotNull
    @Value("${resource.redis.port}")
    private String port;

    @Bean
    @ConditionalOnProperty(name="resource.redis.connect", havingValue="true")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setDefaultSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    @ConditionalOnProperty(name="resource.redis.connect", havingValue="true")
    public RedisConnectionFactory jedisConnectionFactory() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(20);
        poolConfig.setMinIdle(1);
        poolConfig.setMaxWaitMillis(1000);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(6000);

        log.info("REDIS Configuration!!!");

        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(poolConfig);
        jedisConFactory.setHostName(hostname);
        jedisConFactory.setPort(Integer.parseInt(port));

        return jedisConFactory;
    }
}
