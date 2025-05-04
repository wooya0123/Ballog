package notfound.ballog.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.auth.service.AuthService;
import notfound.ballog.domain.auth.request.SignUpRequest;
import notfound.ballog.exception.ValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 회원가입 */
    @PostMapping("/signup")
    public BaseResponse<Void> addUser(
            @Valid @RequestBody SignUpRequest request,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            throw new ValidationException(BaseResponseStatus.BAD_REQUEST);
        }
        authService.signUp(request);
        return BaseResponse.ok();
    }
}
