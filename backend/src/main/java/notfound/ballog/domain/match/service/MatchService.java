package notfound.ballog.domain.match.service;

import lombok.RequiredArgsConstructor;
import notfound.ballog.domain.match.dto.MatchDto;
import notfound.ballog.domain.match.entity.Match;
import notfound.ballog.domain.match.entity.Participant;
import notfound.ballog.domain.match.repository.MatchRepository;
import notfound.ballog.domain.match.repository.ParticipantRepository;
import notfound.ballog.domain.match.repository.StadiumRepository;
import notfound.ballog.domain.match.request.PersonalMatchAddRequest;
import notfound.ballog.domain.match.request.TeamMatchAddRequest;
import notfound.ballog.domain.match.response.MatchDetailResponse;
import notfound.ballog.domain.team.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final StadiumRepository stadiumRepository;
    private final ParticipantRepository participantRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void addPesonalMatch(UUID userId, PersonalMatchAddRequest req){
        Match match = new Match(req);

        matchRepository.save(match);

        participantRepository.save(new Participant(userId, match.getMatchId(), "개인"));
    }

    @Transactional
    public void addTeamMatch(TeamMatchAddRequest req){
        Match match = new Match(req);

        matchRepository.save(match);

        Integer matchId = match.getMatchId();

        List<UUID> userIds = teamMemberRepository.findUserIdsByTeamMemberIds(req.getParticipantList());

        List<Participant> participants = userIds.stream()
                .map(userId -> new Participant(userId, matchId, "팀원"))
                .collect(Collectors.toList());

        participantRepository.saveAll(participants);
    }

    public List<MatchDto> getPesonalMatches(UUID userId, String month){
        return matchRepository.findMatchesByUserIdAndMonth(userId, month);
    }

    public List<MatchDto> getTeamMatches(Integer teamId, String month){
        return matchRepository.findMatchesByTeamIdAndMonth(teamId, month);
    }

    public MatchDetailResponse getMatchDetail(Integer matchId){
        return null;
    }

    public List<String> getStadiums(){
        return stadiumRepository.findAllStadiumNames();
    }

}
