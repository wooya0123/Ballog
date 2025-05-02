package notfound.ballog.domain.team.repository;

import notfound.ballog.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> , TeamRepositoryCustom{

}
