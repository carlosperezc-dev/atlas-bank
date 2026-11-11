package com.atlas.bank.atlas_bank.domain.strategy.fee;

import com.atlas.bank.atlas_bank.domain.model.account.AccountType;


import java.math.BigDecimal;


public class CheckingFeeCalculator implements FeeCalculator{
    @Override
    public boolean supports(AccountType accountType) {

        return AccountType.CHECKING.equals(accountType);
    }

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.015"));
    }
}
