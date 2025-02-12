package com.cattleDB.FrontEndService.service;

import com.cattleDB.FrontEndService.dtos.UserResposeTo;
import com.cattleDB.FrontEndService.models.User;
import com.cattleDB.FrontEndService.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByName(String userName) {
        return userRepo.findByEmail(userName).get();
    }

    public UserResposeTo getUserTo(Authentication authentication) {
        var user = userRepo.findByEmail(authentication.getName());
        return new UserResposeTo(user.get());
    }

    @Transactional
    public User editUser(User newUser, Authentication authentication) {
        var user = userRepo.findByEmail(authentication.getName());
        user.get().setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepo.save(user.get());
    }

    @Override
    public User save(User registrationDto) {
        registrationDto.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        return userRepo.save(registrationDto);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var user = userRepo.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), user.get().getAuthorities());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
}
