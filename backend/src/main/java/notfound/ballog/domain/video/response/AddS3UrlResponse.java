package notfound.ballog.domain.video.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddS3UrlResponse {
    private String s3Url;

    public static AddS3UrlResponse of(String s3Url) {
        return AddS3UrlResponse.builder()
                .s3Url(s3Url)
                .build();
    }
}
