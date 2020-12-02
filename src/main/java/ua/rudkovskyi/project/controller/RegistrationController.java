package ua.rudkovskyi.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.rudkovskyi.project.domain.Role;
import ua.rudkovskyi.project.domain.User;
import ua.rudkovskyi.project.repo.UserRepo;

import java.util.Collections;

@Controller
public class RegistrationController {
    UserRepo userRepo;

    @Autowired
    private void setUserRepo(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        User userFromDB = userRepo.findUserByUsername(user.getUsername());

        if (userFromDB!=null){
            model.addAttribute("message", "User exists!");
            return "registration";
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepo.save(user);
        return "redirect:/login";
    }
}