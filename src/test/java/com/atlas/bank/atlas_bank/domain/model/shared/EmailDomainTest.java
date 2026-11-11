package com.atlas.bank.atlas_bank.domain.model.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailDomainTest {

    @Test
    @DisplayName("Debe crear el email con formato válido")
    void shouldCreateValidEmail(){
        Email email = Email.of("maria@atlasbank.com");

        assertEquals("maria@atlasbank.com", email.getValue());
    }

    @Test
    @DisplayName("Debe normalizar a minusculas")
    void shouldNormalizeToLowerCase(){
        Email email = Email.of("Pedro@Atlas.Bank");
        assertEquals("pedro@atlas.bank",email.getValue());

    }

    @Test
    @DisplayName("Debe rechazar el email con formato invalido")
    void shouldRejectInvalidFormat() {
        assertThrows(IllegalArgumentException.class,
                ()-> Email.of("no es un mail"));
    }

    @Test
    @DisplayName("Debe rechazar el email con valor nulo")
    void shouldRejectNullEmail(){
        assertThrows(IllegalArgumentException.class, ()->
                Email.of(""));
        assertThrows(IllegalArgumentException.class, ()->
                Email.of(null));
    }
}