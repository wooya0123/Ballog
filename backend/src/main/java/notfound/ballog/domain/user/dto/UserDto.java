package notfound.ballog.domain.user.dto;

import lombok.*;
import notfound.ballog.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String nickName;
    private String gender;
    private LocalDate birthDate;
    private String profileImageUrl;
}
