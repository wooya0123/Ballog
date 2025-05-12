package notfound.ballog.domain.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import notfound.ballog.domain.team.request.TeamAddRequest;
import notfound.ballog.domain.team.request.TeamInfoUpdateRequest;
import notfound.ballog.domain.team.request.TeamMemberAddRequest;
import notfound.ballog.domain.team.response.TeamDetailResponse;
import notfound.ballog.domain.team.response.TeamMemberListResponse;
import notfound.ballog.domain.team.response.UserTeamListResponse;
import notfound.ballog.domain.user.repository.PlayerCardRepository;
import notfound.ballog.exception.InternalServerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamCardRepository teamCardRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PlayerCardRepository playerCardRepository;

    @Transactional
    public void addTeam(UUID userId, TeamAddRequest teamAddRequest) {
        Team team = Team.of(teamAddRequest);
        teamRepository.save(team);

        teamCardRepository.save(TeamCard.of(team.getTeamId()));

        teamMemberRepository.save(TeamMember.of(userId, new TeamMemberAddRequest(team.getTeamId(), "MANAGER")));
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
    public void deleteTeam(UUID userId, Integer teamId){
        if(checkTeamMemberRole(userId, teamId)){
            log.error("팀 삭제 에러 발생 {} {}", teamId, userId);
            throw new InternalServerException(BaseResponseStatus.DATABASE_ERROR);
        }


        log.info("팀 삭제 로직 진행");
        teamMemberRepository.deleteAllByTeamId(teamId);

        teamCardRepository.deleteByTeamId(teamId);

        teamRepository.deleteById(teamId);
    }


    @Transactional
    public void deleteTeamMember(UUID userId, Integer teamId, Integer teamMemberId){
        if(checkTeamMemberRole(userId, teamId)){
            log.error("팀 멤버 삭제 에러 발생 {} {}", teamId, teamMemberId);
            throw new InternalServerException(BaseResponseStatus.DATABASE_ERROR);
        }
        
        log.info("팀 멤버 삭제 로직 진행");
        teamMemberRepository.deleteById(teamMemberId);
    }

    private boolean checkTeamMemberRole(UUID userId, Integer teamId){
        String role = teamMemberRepository.findByUserIdAndTeamId(userId, teamId);
        return !role.equals("MANAGER");
    }

    @Transactional
    public void leaveTeam(UUID userId, Integer teamId){
        teamMemberRepository.deleteByUserIdAndTeamId(userId, teamId);
    }

    @Transactional
    public void updateAllTeamCards() {
        List<Team> allTeams = teamRepository.findAll();
        
        log.info("총 {} 개의 팀 카드 업데이트 시작", allTeams.size());
        
        for (Team team : allTeams) {
            try {
                updateTeamCard(team.getTeamId());
            } catch (Exception e) {
                log.error("팀 카드 업데이트 중 오류 발생 - 팀 ID: {}, 오류: {}", team.getTeamId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void updateTeamCard(Integer teamId) {
        // 집계 쿼리 결과를 받아올 DTO 가져오기
        TeamCardDto teamCardDto = playerCardRepository.calculateTeamAverages(teamId);
        
        if (teamCardDto == null || teamCardDto.getMemberCount() == 0) {
            log.info("팀 ID {}에 속한 팀원이 없거나 선수 카드가 없습니다.", teamId);
            return;
        }

        // 팀 카드 업데이트
        TeamCard teamCard = teamCardRepository.findById(teamId)
                .orElseGet(() -> TeamCard.of(teamId));
        
        teamCard.setAvgSpeed(teamCardDto.getAvgSpeed());
        teamCard.setAvgStamina(teamCardDto.getAvgStamina());
        teamCard.setAvgAttack(teamCardDto.getAvgAttack());
        teamCard.setAvgDefense(teamCardDto.getAvgDefense());
        teamCard.setAvgRecovery(teamCardDto.getAvgRecovery());
        
        teamCardRepository.save(teamCard);
        
        log.info("팀 ID {}의 팀 카드 업데이트 완료. 팀원 수: {}", teamId, teamCardDto.getMemberCount());
    }
}
