package com.example.ewallet.wallet;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.Properties;

@Configuration
public class Config {
    // REDIS Configuration


    private JedisConnectionFactory getRedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                "redis-15313.c276.us-east-1-2.ec2.cloud.redislabs.com",
                15313);

        redisStandaloneConfiguration.setPassword("bHGIXH7v80J28BGyZd3k3d9gnnVAPrJi");
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }


    @Bean
    public RedisTemplate<String, Object> getRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(getRedisConnectionFactory());
        return redisTemplate;
    }


    // KAFKA CONFIGURATION

    private Properties getKafkaProperties() {
        Properties properties = new Properties();
        //Producer Configuration properties
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Consumer related properties

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return properties;
    }

    private ProducerFactory<String, String> getProducerFactory() {
        return new DefaultKafkaProducerFactory(getKafkaProperties());
    }

    private ConsumerFactory<String, String> getConsumerFactory() {
        return new DefaultKafkaConsumerFactory(getKafkaProperties());
    }

    // producer bean
    @Bean
    public KafkaTemplate<String, String> getKafkaTemplate() {
        return new KafkaTemplate(getProducerFactory());
    }


    // consumer bean listener factory
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> getKafkaListnerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> listenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerFactory.setConsumerFactory(getConsumerFactory());
        return listenerFactory;
    }


}
