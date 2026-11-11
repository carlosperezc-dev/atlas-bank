package com.atlas.bank.atlas_bank.domain.model.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


class MoneyDomainTest {

    @Test
    @DisplayName("Debe sumar dos montos en la misma moneda")
    void shouldAddSameCurrency(){
        Money a = Money.of(new BigDecimal("100"), Currency.EUR);
        Money b = Money.of(new BigDecimal("50"), Currency.EUR);

        Money result = a.add(b);

        assertEquals(Money.of(new BigDecimal("150"), Currency.EUR), result);

    }

    @Test
    @DisplayName("Debe rechazar la operación entre monedas distintas")
    void shouldRejectDifferenceCurrencies(){
        Money eur = Money.of(new BigDecimal("100"),Currency.EUR);
        Money usd = Money.of(new BigDecimal("50"), Currency.USD);

        assertThrows(IllegalArgumentException.class,
                ()-> eur.add(usd));
    }

    @Test
    @DisplayName("Debe detectar monto negativo")
    void shouldDetectnegativeAmount(){
        Money negative = Money.of(new BigDecimal("-50"), Currency.USD);
        assertTrue(negative.isNegative());
    }

    @Test
    @DisplayName("Debe comparar montos correctamente")
    void shouldCompareAmounts(){
        Money hundred = Money.of(new BigDecimal("100"), Currency.EUR);
        Money fifty = Money.of(new BigDecimal("50"), Currency.EUR);

        assertTrue(hundred.isGreaterThan(fifty));
        assertFalse(hundred.isLessThan(fifty));
    }

    @Test
    @DisplayName("Debe verificar de Money.zero debe tener monto zero")
    void shouldCreateZeroMoney(){
        Money zero = Money.zero(Currency.EUR);
        assertEquals(Money.of(new BigDecimal("0"),Currency.EUR),zero);
    }

}