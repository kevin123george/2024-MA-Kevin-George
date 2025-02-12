package com.cattleDB.FrontEndService.dtos;

import lombok.Data;

@Data
public class LoginUserDto {
    private String email;

    private String password;
}
