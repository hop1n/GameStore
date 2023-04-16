package com.cdprojektred.gamestore.api;

import com.cdprojektred.gamestore.dto.AuthDto;
import com.cdprojektred.gamestore.dto.LoginDto;
import com.cdprojektred.gamestore.security.JWTGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTGenerator jwtGenerator;


    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authenticationManager, jwtGenerator);
    }

    @Test
    void givenTokenTest() {
        LoginDto loginDto = new LoginDto("user123", "password123");
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                loginDto.getPassword());
        String expectedToken = "testToken";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtGenerator.generateToken(authentication)).thenReturn(expectedToken);

        ResponseEntity<AuthDto> response = authController.login(loginDto);

        assertEquals(expectedToken, response.getBody().getAccessToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void badCredentialsExceptionTest() {
        LoginDto loginDto = new LoginDto("badName", "badPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.login(loginDto));
    }

}