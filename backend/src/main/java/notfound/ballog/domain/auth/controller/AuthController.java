package notfound.ballog.domain.auth.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.auth.request.LoginRequest;
import notfound.ballog.domain.auth.request.SendEmailRequest;
import notfound.ballog.domain.auth.request.VerifyEmailRequest;
import notfound.ballog.domain.auth.response.CheckEmailResponse;
import notfound.ballog.domain.auth.response.LoginResponse;
import notfound.ballog.domain.auth.response.TokenRefreshResponse;
import notfound.ballog.domain.auth.service.AuthService;
import notfound.ballog.domain.auth.request.SignUpRequest;
import notfound.ballog.domain.auth.service.EmailService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

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
            @AuthenticationPrincipal(expression="auth.authId") Integer authId){
        authService.logOut(authId);
        return BaseResponse.ok();
    }

    @PostMapping("/refresh-token")
    public BaseResponse<TokenRefreshResponse> refreshToken(
            @AuthenticationPrincipal(expression="auth.authId") Integer authId,
            @RequestHeader("Authorization") String header) {

        String refreshToken = header.replace("Bearer ", "");
        TokenRefreshResponse response = authService.refreshToken(authId, refreshToken);
        return BaseResponse.ok(response);
    }

    @GetMapping("/check-email")
    public BaseResponse<CheckEmailResponse> checkEmail(
            @RequestParam("email")
            @Email(message = "올바른 이메일을 입력하세요.")
            @NotBlank(message = "이메일을 입력하세요.")
            String email
    ){
        CheckEmailResponse response = authService.checkEmail(email);
        return BaseResponse.ok(response);
    }

    @PostMapping("/send-email")
    public BaseResponse<Void> sendEmailCode(@Valid @RequestBody SendEmailRequest request) {
        emailService.sendEmailCode(request);
        return BaseResponse.ok();
    }

    @PostMapping("/verify-email")
    public BaseResponse<Void> verifyEmailCode(@Valid @RequestBody VerifyEmailRequest request) {
        emailService.verifyEmailCode(request);
        return BaseResponse.ok();
    }
}
