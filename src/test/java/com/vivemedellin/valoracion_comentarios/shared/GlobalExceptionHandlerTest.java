package com.vivemedellin.valoracion_comentarios.shared;

import com.vivemedellin.valoracion_comentarios.shared.exceptions.BadRequestException;
import com.vivemedellin.valoracion_comentarios.shared.exceptions.ForbiddenAccessException;
import com.vivemedellin.valoracion_comentarios.shared.exceptions.NotFoundException;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    @Mock
    private DataFetchingEnvironment mockEnv;

   @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockEnv = mock(DataFetchingEnvironment.class);

        Field mockField = mock(Field.class);
        SourceLocation mockLocation = new SourceLocation(1, 1);

        when(mockEnv.getField()).thenReturn(mockField);
        when(mockField.getSourceLocation()).thenReturn(mockLocation);
}

    @Test
    void testNotFoundException() {
        String errorMessage = "Review not found";
        NotFoundException ex = new NotFoundException(errorMessage);

        GraphQLError error = exceptionHandler.resolveToSingleError(ex, mockEnv);

        assertEquals(errorMessage, error.getMessage());
    }

    @Test
    void testBadRequestException() {
        String errorMessage = "Invalid input";
        BadRequestException ex = new BadRequestException(errorMessage);

        GraphQLError error = exceptionHandler.resolveToSingleError(ex, mockEnv);

        assertEquals(errorMessage, error.getMessage());
    }

    @Test
    void testForbiddenAccessException() {
        ForbiddenAccessException ex = new ForbiddenAccessException(); // <-- no message

        GraphQLError error = exceptionHandler.resolveToSingleError(ex, mockEnv);

        assertEquals("Forbidden access", error.getMessage()); // adjust to expected default if needed
    }

    @Test
    void testAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Denied");

        GraphQLError error = exceptionHandler.resolveToSingleError(ex, mockEnv);

        assertEquals("You don't have permission to access this resource", error.getMessage());
    }

    @Test
    void testAuthenticationException() {
        AuthenticationException ex = mock(AuthenticationException.class); // mocked for simplicity

        GraphQLError error = exceptionHandler.resolveToSingleError(ex, mockEnv);

        assertEquals("Invalid or expired token", error.getMessage());
    }

    @Test
    void testUnhandledException() {
        RuntimeException ex = new RuntimeException("Something broke");

        GraphQLError error = exceptionHandler.resolveToSingleError(ex, mockEnv);

        assertEquals("Internal server error", error.getMessage());
    }
}
