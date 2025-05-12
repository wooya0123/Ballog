package notfound.ballog.domain.video.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.video.request.AddVideoRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddVideoResponse {
    private String videoUrl;

    public static AddVideoResponse of(String videoUrl) {
        return AddVideoResponse.builder()
                .videoUrl(videoUrl)
                .build();
    }
}
