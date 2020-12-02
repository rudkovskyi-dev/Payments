package ua.rudkovskyi.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.rudkovskyi.project.domain.Role;
import ua.rudkovskyi.project.domain.User;
import ua.rudkovskyi.project.repo.BalanceRepo;
import ua.rudkovskyi.project.repo.UserRepo;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/users")
public class UserController {
    private UserRepo userRepo;
    private BalanceRepo balanceRepo;

    @Autowired
    private void setUserRepo(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @Autowired
    private void setBalanceRepo(BalanceRepo balanceRepo){
        this.balanceRepo = balanceRepo;
    }

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "users";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "usersEdit";
    }

    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user){
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(user);
        return "redirect:/users";
    }
}
