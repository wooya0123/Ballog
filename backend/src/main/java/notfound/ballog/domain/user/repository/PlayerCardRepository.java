package notfound.ballog.domain.user.repository;

import notfound.ballog.domain.user.entity.PlayerCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerCardRepository extends JpaRepository<PlayerCard, Integer> {
    Optional<PlayerCard> findByUser_UserId(UUID userId);
}
