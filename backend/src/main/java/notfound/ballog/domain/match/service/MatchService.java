package notfound.ballog.domain.match.service;

import lombok.RequiredArgsConstructor;
import notfound.ballog.domain.match.dto.MatchDto;
import notfound.ballog.domain.match.repository.MatchRepository;
import notfound.ballog.domain.match.request.PersonalMatchAddRequest;
import notfound.ballog.domain.match.request.TeamMatchAddRequest;
import notfound.ballog.domain.match.response.MatchDetailResponse;
import notfound.ballog.domain.match.response.StadiumListResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;

    @Transactional
    public void addPesonalMatch(PersonalMatchAddRequest req){

    }

    @Transactional
    public void addTeamMatch(TeamMatchAddRequest req){

    }

    public List<MatchDto> getPesonalMatches(UUID userId, String month){
        return null;
    }

    public List<MatchDto> getTeamMatches(Integer teamId, String month){
        return null;
    }

    public MatchDetailResponse getMatchDetail(Integer matchId){
        return null;
    }

    public StadiumListResponse getStadiums(){
        return null;
    }

}
