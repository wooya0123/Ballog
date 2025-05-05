package notfound.ballog.domain.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateProfileImageRequest {
    @NotNull
    private String profileImageUrl;
}
