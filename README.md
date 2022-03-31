# EWallet


## Requirements and Goals of the System

We will design the system with following requirement and design choice. We will use Spring Boot, Redis, Apache Kafka, MySQL.

### We will build following web service

  1. **User Service** - We will create a user who will use our payment service.
  2. **Wallet Service** - For each user we will create a wallet, through wallet user can send and receive money.
  3. **Transaction Service** - When user transfer money to different user Wallet should be updated for successful transaction.
  4. **Notification Service** -  Send notification for wallet update, transaction update.

### Communication between different service

We will use **Apache Kafka** messaging for communicating between different service.

1. When **User is created** we will produce message on **"USER_CREATE"** topic on kafka cluster.
2. We will configure **Wallet Service** to listen( consume) on **"USER_CREATE"** topic, so whenever user is created, wallet service will get message from topic and create a new wallet, after wallet is created Wallet Service will produce message on **"WALLET_CREATE"** topic.
3. We will configure **Notification Service** to listen on **"WALLET_CREATE"** topic, so whenever wallet is created for user, email service will **send email** to user.
4. **Transaction Service** will create a transaction when user wants to transfer amount to other user. Transaction service will produce message on **"TRANSACTION_CREATE"** topic. 
5. We will configure **Wallet Service**  to listen on **"TRANSACTION_CREATE"** topic, so whenever user make any transaction, wallet service will get message from topic and wallet service will process transaction, validate transaction and update transaction status. 
6. After processing transaction **Wallet Service** will produce message on **"WALLET_UPDATE"** topic. We will configure **Notification Service** to listen on **"WALLET_UPDATE"** topic, so whenever wallet is updated for user, notification service will **send email** to user with updated balance.
7. After processing transaction, **Wallet Service** will produce message on **"TRANSACTION_COMPLETE"** topic with updated transaction status, We will configure **Notification Service** to listen on **"TRANSACTION_COMPLETE"** topic, so whenever transaction is updated for user, notification service will **send email** to user with transaction status.
8. We will also configure **Redis for caching**
 
### Apache Kafka Message Flow

![Kafka Flow](/KafkaDiagram/Kafka.png)

### Project Requirements

1. Install redis - brew install redis
2. Install kafka - brew install kafka
3. Start Zookeeper - Go to *[KAFKA_INSTALL_DIR]* and **run sh /bin/zookeeper-server-start.sh config/zookeeper.properties**
4. Start Kafka server -  Go to *[KAFKA_INSTALL_DIR]* and **run sh bin/kafka-server-start.sh config/server.properties**


### To see messages produced by Kafka, run following command

**sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic "USER_CREATE"**


    
    

