package com.atlas.bank.atlas_bank.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("IllegalStateException debe responder 422 con el mensaje de negocio")
    void shouldMapIllegalStateToUnprocessableEntity(){
        IllegalStateException exception =
                new IllegalStateException("No se puede cerrar una cuenta con saldo");

        ProblemDetail problem = handler.handleIllegalState(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), problem.getStatus());
        assertEquals("No se puede cerrar una cuenta con saldo", problem.getDetail());
        assertNotNull(problem.getTitle());
    }
}
