package notfound.ballog.domain.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.user.request.AddS3ImageUrlRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddS3ImageUrlResponse {
    private String imageUrl;

    public static AddS3ImageUrlResponse of(String imageUrl) {
        return AddS3ImageUrlResponse.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
