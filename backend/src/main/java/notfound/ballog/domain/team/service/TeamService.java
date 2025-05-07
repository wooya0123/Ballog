package notfound.ballog.domain.team.service;

import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.team.dto.PlayerCardDto;
import notfound.ballog.domain.team.dto.TeamCardDto;
import notfound.ballog.domain.team.dto.TeamDto;
import notfound.ballog.domain.team.entity.Team;
import notfound.ballog.domain.team.entity.TeamCard;
import notfound.ballog.domain.team.entity.TeamMember;
import notfound.ballog.domain.team.repository.TeamCardRepository;
import notfound.ballog.domain.team.repository.TeamMemberRepository;
import notfound.ballog.domain.team.repository.TeamRepository;
import notfound.ballog.domain.team.request.*;
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
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamCardRepository teamCardRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void addTeam(UUID userId, TeamAddRequest teamAddRequest) {
        Team team = Team.of(teamAddRequest);
        teamRepository.save(team);

        teamCardRepository.save(TeamCard.of(team.getTeamId()));

        teamMemberRepository.save(TeamMember.of(userId, new TeamMemberAddRequest(team.getTeamId(), "운영진")));
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

        List<PlayerCardDto> playerCardDtoList = teamRepository.findPlayerCardByTeamId(teamId);

        return TeamDetailResponse.of(TeamDto.of(team), TeamCardDto.of(teamCard), playerCardDtoList);
    }

    public TeamMemberListResponse getTeamMemberList(Integer teamId){
        return new TeamMemberListResponse(teamRepository.findAllByTeamId(teamId));
    }

    @Transactional
    public void addTeamMember(UUID userId, TeamMemberAddRequest req){
        teamMemberRepository.save(TeamMember.of(userId, req));
    }

    @Transactional
    public void updateTeamInfo(UUID userId, TeamInfoUpdateRequest req){
        if(checkTeamMemberRole(userId, req.getTeamId())){
            throw new InternalServerException(BaseResponseStatus.DATABASE_ERROR);
        }

        Team team = teamRepository.findById(req.getTeamId())
                .orElseThrow(() -> new InternalServerException(BaseResponseStatus.DATABASE_ERROR));

        team.setTeamName(req.getTeamName());
        team.setFoundationDate(req.getFoundationDate());
        team.setLogoImageUrl(req.getLogoImage());
    }

    // cascade 처리 필요, 매니저가 삭제한다고해서 다른 팀원의 팀들이 사라지는게 맞는가?
    @Transactional
    public void deleteTeam(UUID userId, TeamDeleteRequest req){
        if(checkTeamMemberRole(userId, req.getTeamId())){
            throw new InternalServerException(BaseResponseStatus.DATABASE_ERROR);
        }

        teamRepository.deleteById(req.getTeamId());
    }


    @Transactional
    public void deleteTeamMember(UUID userId, TeamMemberDeleteRequest req){
        if(checkTeamMemberRole(userId, req.getTeamId())){
            throw new InternalServerException(BaseResponseStatus.DATABASE_ERROR);
        }

        teamMemberRepository.deleteById(req.getTeamMemberId());
    }

    private boolean checkTeamMemberRole(UUID userId, Integer teamId){
        String role = teamMemberRepository.findByUserIdAndTeamId(userId, teamId);
        return !role.equals("운영진");
    }

}
