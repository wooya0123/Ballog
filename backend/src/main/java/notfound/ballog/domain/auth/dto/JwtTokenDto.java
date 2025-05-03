package notfound.ballog.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class JwtTokenDto {
    private String grantType;       // "Bearer"
    private String accessToken;
    private String refreshToken;
}
