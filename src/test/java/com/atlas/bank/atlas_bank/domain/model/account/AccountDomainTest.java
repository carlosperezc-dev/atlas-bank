package com.atlas.bank.atlas_bank.domain.model.account;

import com.atlas.bank.atlas_bank.domain.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.exception.InsufficientFundsException;
import com.atlas.bank.atlas_bank.domain.model.shared.Currency;
import com.atlas.bank.atlas_bank.domain.model.shared.Email;
import com.atlas.bank.atlas_bank.domain.model.shared.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountDomainTest {


    private Account createAccountWithBalance(BigDecimal balance){
        return Account.builder()
                .id(1L)
                .accountNumber("ATL-0001-0111-0001-0001")
                .ownerName("Carlos perez")
                .type(AccountType.SAVINGS)
                .balance(Money.of(balance, Currency.EUR))
                .status(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Debe depositar dinero correctamente")
    void shouldDepositMoney(){
        Account account = createAccountWithBalance(new BigDecimal("1000"));
        Money deposit = Money.of(new BigDecimal("500"), Currency.EUR);
        account.deposit(deposit);

        assertEquals(
                Money.of(new BigDecimal("1500"), Currency.EUR), account.getBalance()
        );

    }

    @Test
    @DisplayName("Retirar dinero cuando hay fondos suficientes")
    void shouldWithdrawWhenSufficientFunds(){
        Account account = createAccountWithBalance(new BigDecimal("1000"));
        Money withdraw = Money.of(new BigDecimal("300"), Currency.EUR);

        account.withdraw(withdraw);
        assertEquals(
                Money.of((new BigDecimal("700")), Currency.EUR), account.getBalance()
        );
    }

    @Test
    @DisplayName("Retirar dinero cuando No hay fondos suficientes")
    void shouldWithdrawWhenInsufficientFunds(){
        Account account = createAccountWithBalance(new BigDecimal("1000"));
        Money withdraw = Money.of(new BigDecimal("1200"), Currency.EUR);

       assertThrows(
               InsufficientFundsException.class, ()
                       -> account.withdraw(withdraw)
       );
    }

    @Test
    @DisplayName("Debe rechazar un ingreso de una cantidad positiva")
    void shouldDepositWithNegativeBalance(){
        //Arrange
        Account account = createAccountWithBalance(new BigDecimal("1000"));
        Money deposit = Money.of(new BigDecimal("-10"), Currency.EUR);

        //Act & Assert
        assertThrows(IllegalArgumentException.class, () -> account.deposit(deposit));
    }


    @Test
    @DisplayName("Debe inicializar con valores por defecto")
    void shouldInitializeDefaults(){
        //Account
        Account account = Account.builder()
                .accountNumber("ATL-0002-002-002")
                .ownerName("Alvaro Perez")
                .email(Email.of("Ealvaro@atlas.com"))
                .type(AccountType.CHECKING)
                .build();

        account.initDefaults();
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals(Money.zero(Currency.EUR), account.getBalance());
        assertNotNull(account.getCreatedAt());


    }

    @Test
    @DisplayName("Debe cerrar una cuenta activa con saldo cero")
    void shouldCloseActiveAccountWithZeroBalance(){
        Account account = createAccountWithBalance(BigDecimal.ZERO);

        account.close();

        assertEquals(AccountStatus.CLOSED, account.getStatus());
    }

    @Test
    @DisplayName("No debe cerrar una cuenta que no está activa")
    void shouldRejectCloseWhenAccountIsNotActive(){
        Account account = createAccountWithBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.FROZEN);

        assertThrows(AccountNotActiveException.class, account::close);
        assertEquals(AccountStatus.FROZEN, account.getStatus());
    }

    @Test
    @DisplayName("No debe cerrar una cuenta con saldo distinto de cero")
    void shouldRejectCloseWhenBalanceIsNotZero(){
        Account account = createAccountWithBalance(new BigDecimal("1000"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                account::close
        );

        assertEquals("No se puede cerrar una cuenta con saldo", exception.getMessage());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }
}