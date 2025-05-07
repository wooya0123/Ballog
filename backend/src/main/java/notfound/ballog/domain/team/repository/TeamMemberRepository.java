package notfound.ballog.domain.team.repository;

import notfound.ballog.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {

    @Query("SELECT tm.userId FROM TeamMember tm WHERE tm.teamMemberId IN :teamMemberIds")
    List<UUID> findUserIdsByTeamMemberIds(List<Integer> teamMemberIds);

    @Query("select tm.role from TeamMember tm where tm.userId = :userId and tm.teamId = :teamId")
    String findByUserIdAndTeamId(UUID userId, Integer teamId);
}
