package notfound.ballog.domain.team.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Team", description = "팀 관련 API")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다.")
    public BaseResponse<Void> addTeam(@AuthenticationPrincipal UUID userId, @RequestBody TeamAddRequest teamAddRequest){
        teamService.addTeam(userId, teamAddRequest);
        return BaseResponse.ok();
    }

    @GetMapping
    @Operation(summary = "사용자 팀 목록 조회", description = "로그인한 사용자의 팀 목록을 조회합니다.")
    public BaseResponse<UserTeamListResponse> getUserTeamList(@AuthenticationPrincipal UUID userId){
        return BaseResponse.ok(teamService.getUserTeamList(userId));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀 상세 조회", description = "팀 ID로 팀 상세 정보를 조회합니다.")
    public BaseResponse<TeamDetailResponse> getTeamDetail(@PathVariable Integer teamId){
        return BaseResponse.ok(teamService.getTeamDetail(teamId));
    }

    @GetMapping("/{teamId}/members")
    @Operation(summary = "팀 멤버 목록 조회", description = "팀 ID로 팀 멤버 목록을 조회합니다.")
    public BaseResponse<TeamMemberListResponse> getTeamMemberList(@PathVariable Integer teamId){
        return BaseResponse.ok(teamService.getTeamMemberList(teamId));
    }

    @PostMapping("/invitation")
    @Operation(summary = "팀 멤버 추가", description = "팀에 새로운 멤버를 초대합니다.")
    public BaseResponse<Void> addTeamMember(@AuthenticationPrincipal UUID userId, @RequestBody TeamMemberAddRequest teamMemberAddRequest){
        teamService.addTeamMember(userId, teamMemberAddRequest);
        return BaseResponse.ok();
    }

}
