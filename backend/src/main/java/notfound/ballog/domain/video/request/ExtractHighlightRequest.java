package notfound.ballog.domain.video.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractHighlightRequest {
    private Integer videoId;
    private MultipartFile file;
}