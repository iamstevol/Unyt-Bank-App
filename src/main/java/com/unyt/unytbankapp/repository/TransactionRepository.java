package com.unyt.unytbankapp.repository;

import com.unyt.unytbankapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
