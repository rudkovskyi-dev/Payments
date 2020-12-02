package ua.rudkovskyi.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.rudkovskyi.project.domain.User;
import ua.rudkovskyi.project.repo.UserRepo;

@Controller
public class MainController {
    UserRepo userRepo;

    @Autowired
    private void setUserRepo(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String greeting() {
        return "welcome";
    }

    @GetMapping("/main")
    public String start(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userFromDB = userRepo.findUserByUsername(auth.getName());
        model.addAttribute("user", userFromDB.getId().toString());

        return "main";
    }

}
