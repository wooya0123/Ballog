package notfound.ballog.domain.video.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteVideoRequest {
    @NotNull(message = "영상 아이디를 입력하세요.")
    private Integer videoId;
}
