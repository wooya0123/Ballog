package notfound.ballog.domain.team.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notfound.ballog.domain.team.entity.Team;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static notfound.ballog.domain.team.entity.QTeam.team;
import static notfound.ballog.domain.team.entity.QTeamMember.teamMember;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Team> findAllByUserId(UUID userId) {
        return queryFactory
                .select(team)
                .from(team, teamMember)
                .where(teamMember.userId.eq(userId), teamMember.teamId.eq(team.teamId))
                .fetch();
    }

}
