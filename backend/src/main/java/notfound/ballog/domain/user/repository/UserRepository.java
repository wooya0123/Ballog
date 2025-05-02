package notfound.ballog.domain.user.repository;

import notfound.ballog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
