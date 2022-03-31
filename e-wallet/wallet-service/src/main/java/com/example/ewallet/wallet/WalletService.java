package com.example.ewallet.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class WalletService {

    private static Logger logger = LoggerFactory.getLogger(WalletService.class);
    private final ObjectMapper objectMapper;
    private final WalletRepository walletRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public WalletService(WalletRepository walletRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.walletRepository = walletRepository;
    }


    // Listens on TOPIC : USER_CREATE
    @KafkaListener(topics = {"USER_CREATE"}, groupId = "group1")
    public Wallet createWallet(String msg) throws JsonProcessingException {
        JSONObject walletCreateRequest = objectMapper.readValue(msg, JSONObject.class);

        Wallet wallet = to(walletCreateRequest);
        walletRepository.save(wallet);
        // trigger Wallet creation event
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("walletId", wallet.getId());
        jsonObject.put("userId", wallet.getUserId());
        jsonObject.put("email", wallet.getUserEmail());
        jsonObject.put("balance", wallet.getBalance());
        jsonObject.put("message", "Your Wallet is created with default amount "+wallet.getBalance());
        // p
        kafkaTemplate.send("WALLET_CREATE", objectMapper.writeValueAsString(jsonObject));
        return wallet;


    }


    // This function will listens on TOPIC : TRANSACTION_CREATE
    @KafkaListener(topics = {"TRANSACTION_CREATE"}, groupId = "group4")
    public void updateWallet(String msg) throws Exception {
        JSONObject walletUpdateRequest = objectMapper.readValue(msg, JSONObject.class);

        String sender =(String) walletUpdateRequest.getOrDefault("sender", null);
        String receiver = (String) walletUpdateRequest.getOrDefault("receiver", null);
        Double amount = (Double) walletUpdateRequest.getOrDefault("amount", null);
        String transactionId = (String) walletUpdateRequest.getOrDefault("transactionId", null);
        JSONObject senderWalletUpdateObject = new JSONObject();
        senderWalletUpdateObject.put("transactionId", transactionId);

        if(sender == null || receiver== null  ||  amount == null || amount == 0.0) {
            logger.warn("Either Sender or Receiver is null or amount is null or zero");
            senderWalletUpdateObject.put("transactionStatus", "FAILED");
            kafkaTemplate.send("WALLET_UPDATE", objectMapper.writeValueAsString(senderWalletUpdateObject));
            return;
        }

        Wallet senderWallet = walletRepository.findWalletByUserEmail(sender);
        Wallet receiverWallet = walletRepository.findWalletByUserEmail(receiver);


        if(senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount  ) {
            logger.warn("Either Sender or Receiver wallet doest not exists or sender have insufficient balance");
            senderWalletUpdateObject.put("transactionStatus", "FAILED");
            kafkaTemplate.send("WALLET_UPDATE", objectMapper.writeValueAsString(senderWalletUpdateObject));
            return;
        }

        senderWallet.setBalance(senderWallet.getBalance()-amount);
        receiverWallet.setBalance(receiverWallet.getBalance()+amount);
        walletRepository.saveAll(Arrays.asList(senderWallet, receiverWallet));
        // TODO send wallet update notification event
        senderWalletUpdateObject.put("transactionStatus", "SUCCESS");
        senderWalletUpdateObject.put("email",sender);
        senderWalletUpdateObject.put("message", "Your wallet is debited with amount" + amount + " , new balance is : "+senderWallet.getBalance());
        kafkaTemplate.send("WALLET_UPDATE", objectMapper.writeValueAsString(senderWalletUpdateObject));

        // TODO After successful wallet update send notification for receiver as well.

        JSONObject receiverWalletUpdate = new JSONObject();
        receiverWalletUpdate.put("transactionStatus", "SUCCESS");
        receiverWalletUpdate.put("email",receiver);
        receiverWalletUpdate.put("message", "Your wallet is credited with amount" + amount + " , new balance is : "+receiverWallet.getBalance());

        kafkaTemplate.send("WALLET_UPDATE", objectMapper.writeValueAsString(receiverWalletUpdate));


    }


    private Wallet to(JSONObject obj) {
        //{"country":"IND","amount":10,"nationalId":"12322145","userId":2,"email":"Mitesh.tagadiya@gmail.com"}
        return Wallet.builder()
                .kycId((String)obj.getOrDefault("nationalId", null))
                .country((String)obj.getOrDefault("country",null))
                .userId((Integer)obj.getOrDefault("userId", 0))
                .balance((Double)obj.getOrDefault("amount",0.0))
                .userEmail((String) obj.getOrDefault("email", null))
                .build();

    }


}
