package com.atlas.bank.atlas_bank.domain.model.transaction.state;

import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionStatus;

public record PendingState() implements TransactionState {

    //Vamos a sobreescribir métodos que puede llamar pending,
    //Solo puede pasar a rechazado, o validado desde pending

    @Override
    public TransactionStatus status() {
        return TransactionStatus.PENDING;
    }


    @Override
    public TransactionState reject(String reason) {
        return new RejectedState();
    }

    @Override
    public TransactionState validate() {
        return new RejectedState();
    }
}