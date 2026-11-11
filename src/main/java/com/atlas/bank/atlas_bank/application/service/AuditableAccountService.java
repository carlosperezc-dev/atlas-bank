package com.atlas.bank.atlas_bank.application.service;

import com.atlas.bank.atlas_bank.application.command.CloseAccountCommand;
import com.atlas.bank.atlas_bank.application.command.CreateAccountCommand;
import com.atlas.bank.atlas_bank.application.port.in.CloseAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.CreateAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.GetAccountUseCase;
import com.atlas.bank.atlas_bank.application.port.in.ListAccountsUseCase;
import com.atlas.bank.atlas_bank.domain.model.account.Account;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Primary
public class AuditableAccountService implements
        CreateAccountUseCase, ListAccountsUseCase, GetAccountUseCase, CloseAccountUseCase {

    private final CreateAccountUseCase createAccountUseCase;
    private final ListAccountsUseCase listAccountsUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final CloseAccountUseCase closeAccountUseCase;

    public AuditableAccountService(CreateAccountUseCase createAccountUseCase, ListAccountsUseCase listAccountsUseCase, GetAccountUseCase getAccountUseCase, CloseAccountUseCase closeAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.listAccountsUseCase = listAccountsUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.closeAccountUseCase = closeAccountUseCase;
    }

    @PostConstruct
    public void init() {
        log.info("Clase real del AccountService: {}",createAccountUseCase.getClass().getName() );
        log.info("Clase real del AccountService: {}",listAccountsUseCase.getClass().getName() );
        log.info("Clase real del AccountService: {}",getAccountUseCase.getClass().getName() );
        log.info("Clase real del AccountService: {}",closeAccountUseCase.getClass().getName() );
    }

    @Override
    public Account create(CreateAccountCommand command) {
       log.info("Creando cuenta - número: {}, titular: {}",
               command.accountNumber(), command.ownerName());
       Account created = createAccountUseCase.create(command);
       log.info("Cuenta creada exitosamente - ID: {}", created.getId());
       return created;
    }

    @Override
    public List<Account> findAll() {
        return listAccountsUseCase.findAll();
    }

    @Override
    public Account findById(Long id) {
        return getAccountUseCase.findById(id);
    }

    @Override
    public Account close(CloseAccountCommand command) {
        log.info("Cerrando cuenta - ID: {}", command.accountId());
        Account closed = closeAccountUseCase.close(command);
        log.info("Cuenta cerrada exitosamente - ID: {}, estado: {}",
                closed.getId(), closed.getStatus());
        return closed;
    }
}
