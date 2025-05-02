package notfound.ballog.domain.team.controller;

import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.team.request.TeamAddRequest;
import notfound.ballog.domain.team.request.TeamMemberAddRequest;
import notfound.ballog.domain.team.response.TeamDetailResponse;
import notfound.ballog.domain.team.response.TeamMemberListResponse;
import notfound.ballog.domain.team.response.UserTeamListResponse;
import notfound.ballog.domain.team.service.TeamService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/teams")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public BaseResponse<Void> addTeam(@RequestBody TeamAddRequest teamAddRequest){
        teamService.addTeam(teamAddRequest);
        return BaseResponse.ok();
    }

    @GetMapping
    public BaseResponse<UserTeamListResponse> getUserTeamList(@AuthenticationPrincipal UUID userId){
        return BaseResponse.ok(teamService.getUserTeamList(userId));
    }

    @GetMapping("/{teamId}")
    public BaseResponse<TeamDetailResponse> getTeamDetail(@PathVariable Integer teamId){
        return BaseResponse.ok(teamService.getTeamDetail(teamId));
    }

    @GetMapping("/{teamId}/members")
    public BaseResponse<TeamMemberListResponse> getTeamMemberList(@PathVariable Integer teamId){
        return BaseResponse.ok(teamService.getTeamMemberList(teamId));
    }

    @PostMapping("/invitation")
    public BaseResponse<Void> addTeamMember(@AuthenticationPrincipal UUID userId, @RequestBody TeamMemberAddRequest teamMemberAddRequest){
        teamService.addTeamMember(userId, teamMemberAddRequest);
        return BaseResponse.ok();
    }

}
