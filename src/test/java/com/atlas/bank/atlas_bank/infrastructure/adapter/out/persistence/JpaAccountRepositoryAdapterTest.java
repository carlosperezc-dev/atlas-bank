
package com.atlas.bank.atlas_bank.infrastructure.adapter.out.persistence;

import com.atlas.bank.atlas_bank.domain.model.account.Account;
import com.atlas.bank.atlas_bank.domain.model.account.AccountStatus;
import com.atlas.bank.atlas_bank.domain.model.account.AccountType;
import com.atlas.bank.atlas_bank.domain.model.shared.Currency;
import com.atlas.bank.atlas_bank.domain.model.shared.Email;
import com.atlas.bank.atlas_bank.domain.model.shared.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "ATLAS_DB_USER=atlas",
        "ATLAS_DB_PASSWORD=atlas"
})
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        JpaAccountRepositoryAdapter.class,
        AccountPersistenceMapper.class,
        JpaAccountRepositoryAdapterTest.TestCacheConfiguration.class
})
class JpaAccountRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("atlasbank")
                    .withUsername("atlas")
                    .withPassword("atlas");

    @Autowired
    private JpaAccountRepositoryAdapter accountRepositoryAdapter;

    @Test
    void shouldSaveAccountAndReturnDomainAccount() {

        // Given
        Account account = buildAccount("ES000001");

        // When
        Account savedAccount = accountRepositoryAdapter.save(account);

        // Then
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getAccountNumber()).isEqualTo("ES000001");
        assertThat(savedAccount.getOwnerName()).isEqualTo("Carlos Perez");
        assertThat(savedAccount.getEmail().getValue())
                .isEqualTo("carlos@example.com");
        assertThat(savedAccount.getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(savedAccount.getBalance().getAmount())
                .isEqualByComparingTo("1500.00");
        assertThat(savedAccount.getBalance().getCurrency())
                .isEqualTo(Currency.EUR);
        assertThat(savedAccount.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(savedAccount.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindAccountByIdAndConvertItToDomain() {

        // Given
        Account savedAccount =
                accountRepositoryAdapter.save(buildAccount("ES000002"));

        // When
        Optional<Account> result =
                accountRepositoryAdapter.findById(savedAccount.getId());

        // Then
        assertThat(result).isPresent();

        Account foundAccount = result.orElseThrow();

        assertThat(foundAccount.getId()).isEqualTo(savedAccount.getId());
        assertThat(foundAccount.getAccountNumber()).isEqualTo("ES000002");
        assertThat(foundAccount.getEmail().getValue())
                .isEqualTo("carlos@example.com");
        assertThat(foundAccount.getBalance().getAmount())
                .isEqualByComparingTo("1500.00");
    }

    @Test
    void shouldReturnAllAccounts() {

        // Given
        accountRepositoryAdapter.save(buildAccount("ES000003"));
        accountRepositoryAdapter.save(buildAccount("ES000004"));

        // When
        List<Account> accounts = accountRepositoryAdapter.findAll();

        // Then
        assertThat(accounts).hasSize(2);
        assertThat(accounts)
                .extracting(Account::getAccountNumber)
                .containsExactlyInAnyOrder("ES000003", "ES000004");
    }

    @Test
    void shouldInitializeDefaultValuesWhenSavingAccount() {

        // Given
        Account account = Account.builder()
                .accountNumber("ES000005")
                .ownerName("Carlos Perez")
                .email(Email.of("carlos@example.com"))
                .type(AccountType.SAVINGS)
                .build();

        // When
        Account savedAccount = accountRepositoryAdapter.save(account);

        // Then
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(savedAccount.getBalance().getAmount())
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(savedAccount.getBalance().getCurrency())
                .isEqualTo(Currency.EUR);
        assertThat(savedAccount.getCreatedAt()).isNotNull();
    }

    private Account buildAccount(String accountNumber) {

        return Account.builder()
                .accountNumber(accountNumber)
                .ownerName("Carlos Perez")
                .email(Email.of("carlos@example.com"))
                .type(AccountType.SAVINGS)
                .balance(Money.of(
                        new BigDecimal("1500.00"),
                        Currency.EUR
                ))
                .status(AccountStatus.ACTIVE)
                .build();
    }

    @TestConfiguration
    static class TestCacheConfiguration {

        @Bean
        CacheManager cacheManager() {
            return new NoOpCacheManager();
        }
    }
}
