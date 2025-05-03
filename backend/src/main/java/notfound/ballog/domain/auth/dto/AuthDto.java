package notfound.ballog.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.user.entity.User;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto {
    private User user;
    private String email;
    private String password;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiryDate;
    private Boolean isActive;
}
