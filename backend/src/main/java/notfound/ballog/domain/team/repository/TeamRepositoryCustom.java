package notfound.ballog.domain.team.repository;

import notfound.ballog.domain.team.entity.Team;

import java.util.List;
import java.util.UUID;

public interface TeamRepositoryCustom {

    List<Team> findAllByUserId(UUID userId);

}
