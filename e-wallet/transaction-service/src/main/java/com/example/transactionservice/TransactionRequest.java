package com.example.transactionservice;


import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull
    String sender;

    @NonNull
    String receiver;

    @NonNull
    double amount;

    String memo;

    public Transaction to() {
        return Transaction.builder()
                .receiver(this.receiver)
                .sender(sender)
                .amount(amount)
                .memo(memo)
                .transactionStatus(TransactionStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .build();
    }

}
