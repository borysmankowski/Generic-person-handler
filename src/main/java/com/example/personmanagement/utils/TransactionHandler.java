package com.example.personmanagement.utils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionHandler {

    @Transactional(propagation = Propagation.REQUIRED)
    public void executeInTransaction(VoidSupplier supplier) {
        supplier.get();
    }
}