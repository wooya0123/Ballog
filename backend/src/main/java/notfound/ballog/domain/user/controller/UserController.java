package notfound.ballog.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.user.request.UpdateProfileImageRequest;
import notfound.ballog.domain.user.request.UpdateUserRequest;
import notfound.ballog.domain.user.response.GetPlayerCardResponse;
import notfound.ballog.domain.user.response.GetUserResponse;
import notfound.ballog.domain.user.service.PlayerCardService;
import notfound.ballog.domain.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PlayerCardService playerCardService;

    @GetMapping()
    public BaseResponse<GetUserResponse> getUser(@AuthenticationPrincipal UUID userId) {
        GetUserResponse response = userService.getUser(userId);
        return BaseResponse.ok(response);
    }

    @PatchMapping()
    public BaseResponse<Void> updateUser(@AuthenticationPrincipal UUID userId,
                                         @Valid @RequestBody UpdateUserRequest request
    ) {
        userService.updateUser(userId, request);
        return BaseResponse.ok();
    }

    @PostMapping("/profile-image")
    public BaseResponse<Void> updateProfileImage(@AuthenticationPrincipal UUID userId,
                                                 @Valid @RequestBody UpdateProfileImageRequest request
    ) {
        userService.updateProfileImage(userId, request);
        return BaseResponse.ok();
    }

    @GetMapping("/player-card")
    public BaseResponse<GetPlayerCardResponse> getMyPlayerCard(@AuthenticationPrincipal UUID userId) {
        GetPlayerCardResponse response = playerCardService.getPlayerCard(userId);
        return BaseResponse.ok(response);
    }
}
