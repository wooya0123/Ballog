package notfound.ballog.domain.video.dto;

import lombok.*;
import notfound.ballog.domain.video.entity.Highlight;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighlightDto {
    private Integer highlightId;

    private String highlightName;

    private LocalTime startTime;

    private LocalTime endTime;

    public static HighlightDto of(Highlight highlight) {
        return HighlightDto.builder()
                .highlightId(highlight.getHighlightId())
                .highlightName(highlight.getHighlightName())
                .startTime(highlight.getStartTime())
                .endTime(highlight.getEndTime())
                .build();
    }
}
