package notfound.ballog.domain.auth.repository;

import notfound.ballog.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Integer> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIsActiveTrue(String email);
    Optional<Auth> findByEmail(String email);
}
