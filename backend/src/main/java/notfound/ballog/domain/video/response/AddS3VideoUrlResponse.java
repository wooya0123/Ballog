package notfound.ballog.domain.video.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddS3VideoUrlResponse {
    private String s3Url;

    public static AddS3VideoUrlResponse of(String s3Url) {
        return AddS3VideoUrlResponse.builder()
                .s3Url(s3Url)
                .build();
    }
}
