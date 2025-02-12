package com.cattleDB.FrontEndService.dtos;

import com.cattleDB.FrontEndService.models.User;
import lombok.Data;

@Data
public class UserResposeTo {

    private String userName;


    public UserResposeTo(User user) {
        this.userName = user.getEmail();
    }
}
