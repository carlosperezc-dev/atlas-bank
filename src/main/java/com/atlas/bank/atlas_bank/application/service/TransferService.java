package com.atlas.bank.atlas_bank.application.service;

import com.atlas.bank.atlas_bank.application.command.TransferMoneyCommand;
import com.atlas.bank.atlas_bank.application.port.out.TransactionRepositoryPort;
import com.atlas.bank.atlas_bank.domain.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.model.account.Account;
import com.atlas.bank.atlas_bank.application.port.in.TransferMoneyUseCase;
import com.atlas.bank.atlas_bank.application.port.out.AccountRepositoryPort;
import com.atlas.bank.atlas_bank.domain.model.transaction.TransferContext;
import com.atlas.bank.atlas_bank.domain.model.transaction.Transaction;
import com.atlas.bank.atlas_bank.domain.service.TransferDomainService;
import com.atlas.bank.atlas_bank.domain.strategy.fee.FeeCalculator;
import com.atlas.bank.atlas_bank.domain.validation.TransferValidator;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferService extends TransactionProcessor<TransferContext>
        implements TransferMoneyUseCase {

    private final AccountRepositoryPort accountRepository;
    private final List<FeeCalculator> feeCalculators;
    private final List<TransferValidator> validators;
    private final TransferDomainService transferDomainService;
    public TransferService(TransactionRepositoryPort transactionRepository,
                           AccountRepositoryPort accountRepository,
                           List<FeeCalculator> feeCalculators,
                           ApplicationEventPublisher eventPublisher,
                           List<TransferValidator> validators,
                           TransferDomainService transferDomainService) {
        super(transactionRepository);
        this.accountRepository = accountRepository;
        this.feeCalculators = feeCalculators;
        this.validators = validators;
        this.transferDomainService = transferDomainService;
    }


    @Transactional
    @Override
    public Transaction transfer(TransferMoneyCommand command){
        //buscarCuentas
        Account from = accountRepository.findById(command.fromId())
                .orElseThrow(() -> new AccountNotFoundException(command.fromId()));

        Account to = accountRepository.findById(command.toId())
                .orElseThrow(() -> new AccountNotFoundException(command.toId()));

        Transaction transaction =  process(new TransferContext(from, to, command.amount()));

//        transaction.executeTransfer();
//        transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    protected void validate(TransferContext context) {
        validators.forEach(validator ->
                validator.validate(context));
    }


    @Override
    protected BigDecimal calculateFee(TransferContext context) {
      return feeCalculators.stream()
                .filter(fc -> fc.supports(context.from().getType()))
                .findFirst()
                .orElseThrow( () -> new RuntimeException("No hay calculador para el tipo "+context.from().getType()))
                .calculate(context.amount());
    }

    @Override
    protected void execute(TransferContext context, BigDecimal fee) {
        transferDomainService.transfer(context.from(), context.to(), context.amount(), fee);

        accountRepository.save(context.from());
        accountRepository.save(context.to());
    }

    @Override
    protected Transaction save(TransferContext context, BigDecimal fee) {
        Transaction transaction = TransactionFactory.createTransfer(context,fee);
        return transactionRepository.save(transaction);
    }
}
