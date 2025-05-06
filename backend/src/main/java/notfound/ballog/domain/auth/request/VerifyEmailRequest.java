package notfound.ballog.domain.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerifyEmailRequest {
    @Email(message = "올바른 이메일을 입력하세요.")
    @NotBlank(message = "이메일을 입력하세요.")
    private String email;

    @NotBlank(message = "이메일 인증코드를 입력하세요.")
    private String authCode;
}
