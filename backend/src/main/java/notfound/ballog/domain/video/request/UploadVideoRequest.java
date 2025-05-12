package notfound.ballog.domain.video.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadVideoRequest {

    @NotNull
    private Integer matchId;

    @NotNull(message = "몇 쿼터인지 등록해주세요.")
    private Integer quaterNumber;

    @NotNull(message = "영상을 업로드 해주세요.")
    @NotBlank(message = "영상을 업로드 해주세요.")
    private String videoUrl;

    @NotNull(message = "영상 길이를 입력해주세요.")
    private String duration;
}
