package notfound.ballog.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.user.entity.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto {
    private User user;
    private String email;
    private String password;
    private Boolean isActive;
}
