package notfound.ballog.domain.auth.repository;

import notfound.ballog.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Integer> {
    Boolean existsByEmail(String email);
}
