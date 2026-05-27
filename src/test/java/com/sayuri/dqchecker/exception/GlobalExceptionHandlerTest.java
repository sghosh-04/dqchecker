package com.sayuri.dqchecker.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsInvalidFileToBadRequest() {
        assertEquals(HttpStatus.BAD_REQUEST, handler.handleInvalidFile(new InvalidFileException("bad")).getStatusCode());
    }

    @Test
    void mapsUnauthorizedToUnauthorized() {
        assertEquals(HttpStatus.UNAUTHORIZED,
                handler.handleUnauthorized(new BadCredentialsException("bad credentials")).getStatusCode());
    }

    @Test
    void mapsMissingResourceToNotFound() {
        assertEquals(HttpStatus.NOT_FOUND,
                handler.handleNotFound(new ResourceNotFoundException("missing")).getStatusCode());
    }
}
