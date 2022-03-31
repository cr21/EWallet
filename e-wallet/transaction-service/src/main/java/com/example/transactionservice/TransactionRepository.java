package com.example.transactionservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Integer>{


//    @Modifying
//    @Query("update Transaction t set t.transactionStatus = ?1  where t.transactionId = ?2")
//    void update(TransactionStatus status, String transactionId);

    Transaction findByTransactionId(String trnxId);
}
