package com.atlas.bank.atlas_bank.domain.model.transaction.state;

import com.atlas.bank.atlas_bank.domain.model.transaction.TransactionStatus;

public sealed interface TransactionState permits PendingState,
        ValidatedState,
        ExecutedState,
        RejectedState,
        ReversedState{

    public TransactionStatus status();

    //default en una interfaz es un metodo  que viene implementado por defecto (de fábrica)
    default TransactionState execute(){
        throw new IllegalStateException("No se puede ejecutar una transacción en estado: "+status());
    }

    default TransactionState  validate(){
        throw new IllegalStateException(
                "No se puede validar una transacción en estado: "+ status()
        );
    }

    default TransactionState  reject(String reason){
        throw new IllegalStateException(
                "No se puede rechazar una transacción en estado: "+ status()
        );
    }

    default TransactionState reverse(){
        throw new IllegalStateException(
                "No se puede revertir una transacción en estado: "+ status()
        );
    }
}
