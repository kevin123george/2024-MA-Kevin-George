package com.cattleDB.FrontEndService.service;


import com.cattleDB.FrontEndService.dtos.UserResposeTo;
import com.cattleDB.FrontEndService.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

public interface UserService extends UserDetailsService {
    User save(User registrationDto);

    User editUser(User user, Authentication authentication);

    Collection<? extends GrantedAuthority> getAuthorities();

    User getUserByName(String name);

    UserResposeTo getUserTo(Authentication authentication);


}
