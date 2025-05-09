package notfound.ballog.domain.video.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuarterDto {
    private Integer videoId;
    private Integer quarterNumber;
    private String videoUrl;
    private HighlightListDto highlightList;
}
