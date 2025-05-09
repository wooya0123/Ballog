package notfound.ballog.domain.video.response;

import lombok.*;
import notfound.ballog.domain.video.dto.QuarterListDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetVideoListResponse {
    private Integer totalQuarters;
    private QuarterListDto quarterList;
}
