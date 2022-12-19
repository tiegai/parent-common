package com.nike.ncp.common.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ApiExceptionsTest {

    @Test
    public void internalError() {
        ApiException ex = ApiExceptions.internalError();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertEquals(1, ex.getErrors().size());
    }

    @Test
    public void itemNotFound() {
        ApiException ex = ApiExceptions.itemNotFound();
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals(1, ex.getErrors().size());
    }

    @Test
    public void accessDenied() {
        ApiException ex = ApiExceptions.accessDenied();
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertEquals(1, ex.getErrors().size());
    }

    @Test
    public void invalidRequest() {
        ApiException ex = ApiExceptions.invalidRequest().with(100, "message");
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals(1, ex.getErrors().size());
    }
}
