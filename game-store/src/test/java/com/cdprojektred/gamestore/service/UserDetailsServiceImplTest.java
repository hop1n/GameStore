package com.cdprojektred.gamestore.service;

import com.cdprojektred.gamestore.exceptions.UserNotFoundException;
import com.cdprojektred.gamestore.model.UserEntity;
import com.cdprojektred.gamestore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername() {
        UserEntity user = new UserEntity(1L,"test", "test", "ROLE_USER");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test");

        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.getRole(), userDetails.getAuthorities().iterator().next().getAuthority());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void loadUserByWrongUserName() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userDetailsService.loadUserByUsername("test"));
        verify(userRepository, times(1)).findByUsername(anyString());
    }
}