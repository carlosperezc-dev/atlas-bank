package com.atlas.bank.atlas_bank.infrastructure.adapter.in.rest.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentAccountsValidator implements ConstraintValidator<DifferentAccounts, TransferRequest> {
    @Override
    public boolean isValid(TransferRequest transferRequest, ConstraintValidatorContext context) {
        if (transferRequest.getSourceAccountId()==null || transferRequest.getTargetAccountId()==null)
            return true;
        return !transferRequest.getSourceAccountId().equals(transferRequest.getTargetAccountId());
    }
}
