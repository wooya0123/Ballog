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

    public static GetVideoListResponse of(Integer totalQuarters, QuarterListDto quarterList) {
        return GetVideoListResponse.builder()
                .totalQuarters(totalQuarters)
                .quarterList(quarterList)
                .build();
    }
}
