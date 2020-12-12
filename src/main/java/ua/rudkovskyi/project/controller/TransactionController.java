package ua.rudkovskyi.project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.rudkovskyi.project.domain.Balance;
import ua.rudkovskyi.project.domain.Role;
import ua.rudkovskyi.project.domain.Transaction;
import ua.rudkovskyi.project.domain.User;
import ua.rudkovskyi.project.repo.BalanceRepo;
import ua.rudkovskyi.project.repo.TransactionRepo;
import ua.rudkovskyi.project.repo.UserRepo;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/u")
public class TransactionController {
    UserRepo userRepo;
    BalanceRepo balanceRepo;
    TransactionRepo transactionRepo;

    @Autowired
    private void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    private void setBalanceRepo(BalanceRepo balanceRepo) {
        this.balanceRepo = balanceRepo;
    }

    @Autowired
    private void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @GetMapping("{user}/{balance}")
    public String transaction(@PathVariable User user, @PathVariable Balance balance, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = checkAuthorization(auth, user, balance);
        Iterable<Transaction> transactions = transactionRepo.findBySourceOrDestination(balance, balance);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("transactions", transactions);

        return "transaction";
    }

    @PostMapping("{user}/{balance}")
    public String submitPayment(@PathVariable User user,
                                @PathVariable Balance balance,
                                @RequestParam("sourceId") Balance source,
                                @RequestParam("destinationId") Balance destination,
                                @RequestParam Double doubleAmount,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user, balance);
        if (destination == null) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        doubleAmount *= 100;
        Transaction transaction = new Transaction(source, destination, doubleAmount.longValue());
        transactionRepo.save(transaction);

        return "redirect:/u/" + user.getId() + "/" + balance.getId();
    }

    @GetMapping("{user}/{balance}/pay")
    public String makePayment(@PathVariable User user, @PathVariable Balance balance) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user, balance);

        return "pay";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}/{balance}/{transaction}")
    public String transactionInfo(@PathVariable User user,
                                  @PathVariable Balance balance,
                                  @PathVariable Transaction transaction) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user, balance);
        if (transaction == null || !transaction.getSource().equals(balance)) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        return "transactionInfo";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("{user}/{balance}/{transaction}")
    public String transactionUpdate(@PathVariable User user,
                                    @PathVariable Balance balance,
                                    @PathVariable Transaction transaction) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user, balance);
        if (
                transaction == null ||
                        !transaction.getSource().equals(balance) ||
                        transaction.isSent() ||
                        balance.isLocked()) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        long amountSource = balance.getAmount() - transaction.getAmount();
        long amountDestination = transaction.getDestination().getAmount() + transaction.getAmount();
        if (amountSource < 0) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        balance.setAmount(amountSource);
        transaction.getDestination().setAmount(amountDestination);
        transaction.setSent(true);
        balanceRepo.save(balance);
        balanceRepo.save(transaction.getDestination());
        transactionRepo.save(transaction);

        return "redirect:/u/" + user.getId() + "/" + balance.getId();
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("{user}/{balance}/{transaction}")
    public String transactionDelete(@PathVariable User user,
                                    @PathVariable Balance balance,
                                    @PathVariable Transaction transaction) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAuthorization(auth, user, balance);

        if (
                transaction == null ||
                        !transaction.getSource().equals(balance) ||
                        transaction.isSent()
        ) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        transactionRepo.delete(transaction);

        return "redirect:/u/" + user.getId() + "/" + balance.getId();
    }

    public boolean checkAuthorization(Authentication auth, User user, Balance balance) {
        if (user == null || balance == null) {
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
        if (!(auth.getName().equals(user.getUsername()) && user.getUsername().equals(balance.getOwnerName()))) {
            if (!isAdmin) {
                throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
            }
        }
        return isAdmin;
    }
}
