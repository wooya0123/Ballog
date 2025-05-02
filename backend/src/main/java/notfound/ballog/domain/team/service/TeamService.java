package notfound.ballog.domain.team.service;

import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.team.dto.TeamCardDto;
import notfound.ballog.domain.team.dto.TeamDto;
import notfound.ballog.domain.team.entity.Team;
import notfound.ballog.domain.team.entity.TeamCard;
import notfound.ballog.domain.team.entity.TeamMember;
import notfound.ballog.domain.team.repository.TeamCardRepository;
import notfound.ballog.domain.team.repository.TeamMemberRepository;
import notfound.ballog.domain.team.repository.TeamRepository;
import notfound.ballog.domain.team.request.TeamAddRequest;
import notfound.ballog.domain.team.request.TeamMemberAddRequest;
import notfound.ballog.domain.team.response.TeamDetailResponse;
import notfound.ballog.domain.team.response.TeamMemberListResponse;
import notfound.ballog.domain.team.response.UserTeamListResponse;
import notfound.ballog.exception.InternalServerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamCardRepository teamCardRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void addTeam(TeamAddRequest teamAddRequest) {
        Team team = Team.of(teamAddRequest);
        teamRepository.save(team);

        teamCardRepository.save(TeamCard.of(team.getTeamId()));
    }

    public UserTeamListResponse getUserTeamList(UUID userId){
        List<TeamDto> teamDtoList = teamRepository.findAllByUserId(userId).stream()
                .map(TeamDto::of)
                .toList();

        return new UserTeamListResponse(teamDtoList);
    }

    public TeamDetailResponse getTeamDetail(Integer teamId){
        Team team = teamRepository.findById(teamId).orElse(null);
        TeamCard teamCard = teamCardRepository.findById(teamId).orElse(null);

        if(team == null || teamCard == null){
            throw new InternalServerException(BaseResponseStatus.DATABASE_ERROR);
        }

        // List<PlayerCard> playerCardList = ?

        return TeamDetailResponse.of(TeamDto.of(team), TeamCardDto.of(teamCard));
    }

    public TeamMemberListResponse getTeamMemberList(Integer teamId){
        return null;
    }

    @Transactional
    public void addTeamMember(UUID userId, TeamMemberAddRequest req){
        teamMemberRepository.save(TeamMember.of(userId, req));
    }

}
