package com.example.ewallet.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private  final UserRepository userRepository;
    private  final UserCacheRepository userCacheRepository;
    private  final KafkaTemplate<String, String> kafkaTemplate;
    private Double onboardingAmount;
    private  final ObjectMapper objectMapper;


    // constructor injection
    UserService(UserRepository userRepository, UserCacheRepository userCacheRepository, KafkaTemplate<String, String> kafkatemplate,
                @Value("${user.boarding.reward}") double onboardingAmount){
        this.onboardingAmount = onboardingAmount;
        this.kafkaTemplate = kafkatemplate;
        this.userCacheRepository = userCacheRepository;
        this.userRepository = userRepository;
        objectMapper  = new ObjectMapper();
    }

    public void createUser(UserCreateRequest userCreateRequest) throws JsonProcessingException {

        User user = userCreateRequest.to();
        // save in db
        user = userRepository.save(user);
        // cache in redis
        userCacheRepository.save(user);


        // Trigger User Creation Event Kafka Event to create wallet for user with fix amount
        // id, email, nationalId, country, fixed_amount we need to add in new wallet
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", user.getId());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("nationalId", user.getKycId());
        jsonObject.put("country", user.getCountry());
        jsonObject.put("amount", this.onboardingAmount );
        kafkaTemplate.send("USER_CREATE", objectMapper.writeValueAsString(jsonObject));
    }

    public User getUser(int userId) {
        // check in redis cache
        User user = userCacheRepository.get(userId);
        if(user == null ) {
            user =  userRepository.findById(userId).orElse(null);
            if(user != null){
                // store in cache if you found data in database
                userCacheRepository.save(user);
            }
        }
        return user;
    }
}
