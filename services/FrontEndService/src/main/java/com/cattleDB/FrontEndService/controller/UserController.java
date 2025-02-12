package com.cattleDB.FrontEndService.controller;


import com.cattleDB.FrontEndService.models.Role;
import com.cattleDB.FrontEndService.models.User;
import com.cattleDB.FrontEndService.repository.RoleRepositroy;
import com.cattleDB.FrontEndService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RoleRepositroy roleRepositroy;

    @Autowired
    public UserController(UserService userService, RoleRepositroy roleRepositroy) {
        super();
        this.userService = userService;
        this.roleRepositroy = roleRepositroy;
    }

    @ModelAttribute("user")
    public User user() {
        return new User();
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepositroy.findAll());
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@Valid @ModelAttribute("user") User user, BindingResult error, Model model) {
        if (error.hasErrors()) {
            model.addAttribute("roles", roleRepositroy.findAll());
            return "registration";
        }
        Role role = roleRepositroy.findById(user.getRoleId()).orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));
        user.setRoles(List.of(role));
        userService.save(user);
        return "redirect:/user/login";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }


}
