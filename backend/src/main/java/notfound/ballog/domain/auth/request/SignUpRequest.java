package notfound.ballog.domain.auth.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SignUpRequest {
    private String email;
    private String password;
    private String gender;
    private String nickName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String profileImageUrl;
}
