package com.example.transactionservice;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TransactionController {


    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService ) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public String createTransaction(@RequestBody @Valid TransactionRequest transactionRequest) throws JsonProcessingException {

        return transactionService.processTransaction(transactionRequest);

    }

}
