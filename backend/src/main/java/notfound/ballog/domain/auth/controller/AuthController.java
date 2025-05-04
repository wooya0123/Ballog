package notfound.ballog.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.auth.request.LoginRequest;
import notfound.ballog.domain.auth.response.LoginResponse;
import notfound.ballog.domain.auth.service.AuthService;
import notfound.ballog.domain.auth.request.SignUpRequest;
import notfound.ballog.domain.auth.service.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public BaseResponse<Void> addUser(@Valid @RequestBody SignUpRequest request){
        authService.signUp(request);
        return BaseResponse.ok();
    }

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return BaseResponse.ok(response);
    }

    @PostMapping("/logout")
    public BaseResponse<Void> logout(
            @AuthenticationPrincipal(expression="auth.id") Integer authId,
            @RequestHeader("Authorization") String header){

        String accessToken = header.replace("Bearer ", "");
        authService.logOut(authId, accessToken);
        return BaseResponse.ok();
    }
}
