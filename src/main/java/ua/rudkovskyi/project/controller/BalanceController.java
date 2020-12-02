package ua.rudkovskyi.project.controller;

import com.sun.corba.se.spi.ior.ObjectKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import ua.rudkovskyi.project.domain.Role;
import ua.rudkovskyi.project.domain.User;
import ua.rudkovskyi.project.repo.UserRepo;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/u")
public class BalanceController {
    UserRepo userRepo;

    @Autowired
    private void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("{user}")
    public String balance(@PathVariable User user) {
        if (user==null) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User userFromDB = userRepo.findUserByUsername(auth.getName());
        boolean isAllowed = false;
        for (Role r : userFromDB.getRoles()) {
            if ("ADMIN".equals(r.getAuthority())) {
                isAllowed = true;
                break;
            }
        }

        if (!auth.getName().equals(user.getUsername())) {
            if (!isAllowed) {
                throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
            }
        }

        return "balance";
    }
}
