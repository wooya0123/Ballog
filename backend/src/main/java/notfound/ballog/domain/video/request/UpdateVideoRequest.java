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
public class UpdateVideoRequest {
    @NotNull(message = "매치 아이디를 입력하세요.")
    private Integer matchId;

    @NotNull(message = "쿼터 번호를 입력하세요.")
    private Integer quarterNumber;
}
