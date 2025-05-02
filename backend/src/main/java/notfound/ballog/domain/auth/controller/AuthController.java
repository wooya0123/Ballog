package notfound.ballog.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.auth.service.AuthService;
import notfound.ballog.domain.auth.request.SignUpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public BaseResponse addUser(@RequestBody SignUpRequest request){
        authService.addAuth(request);
        return BaseResponse.ok();
    }
}
