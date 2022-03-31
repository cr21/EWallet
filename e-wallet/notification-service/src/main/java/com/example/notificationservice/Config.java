package com.example.notificationservice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class Config {

    private Properties getKafkaProperties() {
        Properties properties = new Properties();
        // Consumer related properties

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return properties;
    }

    private ConsumerFactory<String, String> getConsumerFactory() {
        return new DefaultKafkaConsumerFactory(getKafkaProperties());
    }

    // consumer bean listner factory
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> getKafkaListnerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> listenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerFactory.setConsumerFactory(getConsumerFactory());
        return listenerFactory;
    }


    // email sending configuration
    @Bean
    JavaMailSender getMailSender() {
        JavaMailSenderImpl javaMailSenderImp = new JavaMailSenderImpl();
        javaMailSenderImp.setHost("smtp.gmail.com");
        javaMailSenderImp.setUsername("chiragwallet1@gmail.com");
        javaMailSenderImp.setPassword("3edc#EDC");
        javaMailSenderImp.setPort(587);
        Properties properties = javaMailSenderImp.getJavaMailProperties();
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.debug", true); // get debug message of java mail sender
        return  javaMailSenderImp;

    }


    @Bean
    SimpleMailMessage getMailMessage(){
        return new SimpleMailMessage();
    }

}
