package notfound.ballog.domain.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateUserRequest {
    @NotBlank(message = "성별을 입력해주세요.")
    private String gender;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotNull
    private LocalDate birthDate;
}
