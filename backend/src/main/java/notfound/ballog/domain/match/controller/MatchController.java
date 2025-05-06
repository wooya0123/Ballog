package notfound.ballog.domain.match.controller;

import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.match.request.PersonalMatchAddRequest;
import notfound.ballog.domain.match.request.TeamMatchAddRequest;
import notfound.ballog.domain.match.response.MatchDetailResponse;
import notfound.ballog.domain.match.response.PersonalMatchListResponse;
import notfound.ballog.domain.match.response.StadiumListResponse;
import notfound.ballog.domain.match.response.TeamMatchListResponse;
import notfound.ballog.domain.match.service.MatchService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/matches")
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/me")
    public BaseResponse<Void> addPesonalMatch(@AuthenticationPrincipal UUID userId, @RequestBody PersonalMatchAddRequest pesonalMatchAddRequest) {
        matchService.addPesonalMatch(userId ,pesonalMatchAddRequest);
        return BaseResponse.ok();
    }

    @PostMapping("/teams")
    public BaseResponse<Void> addTeamMatch(@RequestBody TeamMatchAddRequest teamMatchAddRequest) {
        matchService.addTeamMatch(teamMatchAddRequest);
        return BaseResponse.ok();
    }

    @GetMapping("/me")
    public BaseResponse<PersonalMatchListResponse> getPesonalMatches(@AuthenticationPrincipal UUID userId, @RequestParam("month") String month) {
        PersonalMatchListResponse response = new PersonalMatchListResponse(matchService.getPesonalMatches(userId,month));
        return BaseResponse.ok(response);
    }

    @GetMapping("/teams/{teamId}")
    public BaseResponse<TeamMatchListResponse> getTeamMatches(@PathVariable Integer teamId, @RequestParam("month") String month) {
        TeamMatchListResponse response = new TeamMatchListResponse(matchService.getTeamMatches(teamId,month));
        return BaseResponse.ok(response);
    }

    @GetMapping("/{matchId}")
    public BaseResponse<MatchDetailResponse> getMatchDetail(@PathVariable Integer matchId){
        return BaseResponse.ok(matchService.getMatchDetail(matchId));
    }

    @GetMapping("/stadiums")
    public BaseResponse<StadiumListResponse> getStadiums(){
        StadiumListResponse resp = new StadiumListResponse(matchService.getStadiums());
        return BaseResponse.ok(resp);
    }

}
