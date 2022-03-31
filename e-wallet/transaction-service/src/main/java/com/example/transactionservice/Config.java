package com.example.transactionservice;


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




    // KAFKA CONFIGURATION

    // Producer Configuration
    // ProduerConfig class contains information about producer configuration
    // producer properties
    // key value serializer
    // bootstrap servers
    // topic name
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


    // consumer bean listner factory
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> getKafkaListnerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> listenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerFactory.setConsumerFactory(getConsumerFactory());
        return listenerFactory;
    }

}
