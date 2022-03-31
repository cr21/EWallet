package com.example.notificationservice;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final JavaMailSender javamailsender;
    private final SimpleMailMessage simpleMailMessage;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public EmailService( KafkaTemplate<String, String> kafkaTemplate, JavaMailSender javaMailSender, SimpleMailMessage simpleMailMessage) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.javamailsender = javaMailSender;
        this.simpleMailMessage =  simpleMailMessage;
    }

    // consumer
    // this function will listens on user_Create topic
    @KafkaListener(topics = {"WALLET_CREATE","WALLET_UPDATE","TRANSACTION_COMPLETE"}, groupId = "group2")
    public void sendEmail(String msg) throws Exception {
        JSONObject walletCreateObject = objectMapper.readValue(msg, JSONObject.class);

        String emailBody =  (String)walletCreateObject.getOrDefault("message", null);
        String email = (String)walletCreateObject.getOrDefault("email", null);

        if(email == null) {
            throw new  Exception("Invalid Email");
        }

        simpleMailMessage.setText(emailBody);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom("chiragwallet1@gmail.com");
        simpleMailMessage.setSubject("Wallet Update");

        javamailsender.send(simpleMailMessage);

    }

}
