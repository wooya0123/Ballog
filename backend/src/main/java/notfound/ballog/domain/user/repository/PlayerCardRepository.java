package notfound.ballog.domain.user.repository;

import notfound.ballog.domain.team.dto.TeamCardDto;
import notfound.ballog.domain.user.entity.PlayerCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PlayerCardRepository extends JpaRepository<PlayerCard, Integer> {
    Optional<PlayerCard> findByUser_UserId(UUID userId);

    @Query("""
    SELECT new notfound.ballog.domain.team.dto.TeamCardDto(
        COUNT(pc.cardId),\s
        CAST(COALESCE(AVG(pc.speed), 0) AS INTEGER),\s
        CAST(COALESCE(AVG(pc.stamina), 0) AS INTEGER),\s
        CAST(COALESCE(AVG(pc.attack), 0) AS INTEGER),\s
        CAST(COALESCE(AVG(pc.defense), 0) AS INTEGER),\s
        CAST(COALESCE(AVG(pc.recovery), 0) AS INTEGER)
    )
    FROM PlayerCard pc
    JOIN TeamMember tm ON pc.user.userId = tm.userId
    WHERE tm.teamId = :teamId
   \s""")
    TeamCardDto calculateTeamAverages(Integer teamId);

}
