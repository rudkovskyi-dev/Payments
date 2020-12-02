package ua.rudkovskyi.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.rudkovskyi.project.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
