package com.cdprojektred.gamestore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginDto{
    private String username;
    private String password;
}