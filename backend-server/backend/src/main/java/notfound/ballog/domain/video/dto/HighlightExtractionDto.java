package notfound.ballog.domain.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.video.entity.Highlight;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighlightExtractionDto {
    private Integer highlightId;

    private String highlightName;

    private LocalTime startTime;

    private LocalTime endTime;

    public static HighlightExtractionDto of(Highlight highlight) {
        return HighlightExtractionDto.builder()
                .highlightId(highlight.getHighlightId())
                .highlightName(highlight.getHighlightName())
                .startTime(highlight.getStartTime())
                .endTime(highlight.getEndTime())
                .build();
    }
}
