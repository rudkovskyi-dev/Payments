package ua.rudkovskyi.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.rudkovskyi.project.domain.Balance;
import ua.rudkovskyi.project.domain.Transaction;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceOrDestination(Balance balanceSource, Balance balanceDestination);
}
