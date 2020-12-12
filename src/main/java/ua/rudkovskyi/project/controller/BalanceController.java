package ua.rudkovskyi.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.rudkovskyi.project.domain.Balance;
import ua.rudkovskyi.project.domain.Role;
import ua.rudkovskyi.project.domain.User;
import ua.rudkovskyi.project.repo.BalanceRepo;
import ua.rudkovskyi.project.repo.UserRepo;

import static java.lang.Math.round;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/u")
public class BalanceController {
    UserRepo userRepo;
    BalanceRepo balanceRepo;

    @Autowired
    private void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    private void setBalanceRepo(BalanceRepo balanceRepo){
        this.balanceRepo = balanceRepo;
    }

    @GetMapping("{user}")
    public String balance(@PathVariable User user, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = checkAuthorization(auth, user);
        Iterable<Balance> balances = balanceRepo.findByOwner(user);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("balances", balances);

        return "balance";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}/new")
    public String newBalance(@PathVariable User user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user);
        return "newBalance";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("{user}/new")
    public String createBalance(@PathVariable User user,
                              @RequestParam String name,
                              @RequestParam Double amount,
                              Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user);
        amount*=100;
        Balance balance = new Balance(name, amount.longValue(), user);
        balanceRepo.save(balance);
        return "redirect:/u/"+user.getId();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}/edit")
    public String editBalance(@PathVariable User user,
                              @RequestParam Balance balance,
                              Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user);
        model.addAttribute("balance", balance);
        return "editBalance";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("{user}")
    public String updateBalance(@PathVariable User user,
                                @RequestParam("balanceId") Balance balance,
                                @RequestParam String name,
                                @RequestParam Double doubleAmount,
                                @RequestParam boolean isLocked) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user);
        balance.setName(name);
        doubleAmount*=100;
        balance.setAmount(doubleAmount.longValue());
        balance.setLocked(isLocked);
        if (!isLocked){
            balance.setRequested(false);
        }

        balanceRepo.save(balance);
        return "redirect:/u/"+user.getId();
    }

    @GetMapping("{user}/request")
    public String requestUnlock(@PathVariable User user,
                                @RequestParam Balance balance,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user);
        if (balance.isRequested()) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        balance.setRequested(true);
        balanceRepo.save(balance);
        return "redirect:/u/"+user.getId();
    }

    @GetMapping("{user}/lock")
    public String lockBalance(@PathVariable User user,
                                @RequestParam Balance balance,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user);
        if (balance.isLocked()) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        balance.setLocked(true);
        balanceRepo.save(balance);
        return "redirect:/u/"+user.getId();
    }

    public boolean checkAuthorization(Authentication auth, User user){
        if (user==null) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }

        User userFromDB = userRepo.findUserByUsername(auth.getName());
        boolean isAdmin = false;
        for (Role r : userFromDB.getRoles()) {
            if ("ADMIN".equals(r.getAuthority())) {
                isAdmin = true;
                break;
            }
        }
        if (!auth.getName().equals(user.getUsername())) {
            if (!isAdmin) {
                throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
            }
        }
        return isAdmin;
    }
}
