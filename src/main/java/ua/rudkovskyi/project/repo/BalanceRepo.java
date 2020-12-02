package ua.rudkovskyi.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.rudkovskyi.project.domain.Balance;
import ua.rudkovskyi.project.domain.User;

import java.util.List;

public interface BalanceRepo extends JpaRepository<Balance, Long> {
    List<Balance> findByOwner(User owner);
}
