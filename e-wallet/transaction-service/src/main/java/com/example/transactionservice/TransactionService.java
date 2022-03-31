package com.example.transactionservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private  final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public TransactionService(TransactionRepository transactionRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
    }

    public String processTransaction(TransactionRequest transactionRequest) throws JsonProcessingException {
        Transaction transaction = transactionRequest.to();
        transactionRepository.save(transaction);

        // TODO : Notify Wallet Service to updated Wallet Balance and check validity of transaction
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", transaction.getSender());
        jsonObject.put("receiver", transaction.getReceiver());
        jsonObject.put("amount", transaction.getAmount());
        jsonObject.put("transactionId", transaction.getTransactionId());
//        jsonObject.put("transactionStatus", transaction.getTransactionStatus());

        // Produce Transaction Create Event which will be consumed by WalletService
        kafkaTemplate.send("TRANSACTION_CREATE", objectMapper.writeValueAsString(jsonObject));
        // send transaction ID to front end user
        return transaction.getTransactionId();

    }

//    @KafkaListener(topics = {"TRANSACTION_CREATE"}, groupId = "group4")
//    public void updateWallet(String msg) throws Exception {
//        JSONObject walletUpdateRequest = objectMapper.readValue(msg, JSONObject.class);
//
//        String sender =(String) walletUpdateRequest.getOrDefault("sender", null);
//        String receiver = (String) walletUpdateRequest.getOrDefault("receiver", null);
//        Double amount = (Double) walletUpdateRequest.getOrDefault("amount", null);
//        String transactionId = (String) walletUpdateRequest.getOrDefault("transactionId", null);
//        JSONObject senderWalletUpdateObject = new JSONObject();
//        senderWalletUpdateObject.put("transactionId", transactionId);

    @KafkaListener(topics = {"WALLET_UPDATE"}, groupId = "group6")
    public void updateTransaction(String message) throws Exception {
        JSONObject transactionUpdate = objectMapper.readValue(message, JSONObject.class);
        String transactionId = (String) transactionUpdate.getOrDefault("transactionId", null);

        if(transactionId == null) {
            logger.warn("transactionId is not present in event so can not update transaction Status");
            return;
        }

        String transactionStatus = (String) transactionUpdate.getOrDefault("transactionStatus","PENDING");

        TransactionStatus status = TransactionStatus.valueOf(transactionStatus);
        Transaction tranFrmDB = transactionRepository.findByTransactionId(transactionId);
        tranFrmDB.setTransactionStatus(status);
        tranFrmDB = transactionRepository.save(tranFrmDB);

        // SEND Transaction complete event
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Hi Your Transaction with id " + tranFrmDB.getTransactionId() + " is " +tranFrmDB.getTransactionStatus());
        jsonObject.put("email", tranFrmDB.getSender());
        kafkaTemplate.send("TRANSACTION_COMPLETE", objectMapper.writeValueAsString(jsonObject));
    }
}
