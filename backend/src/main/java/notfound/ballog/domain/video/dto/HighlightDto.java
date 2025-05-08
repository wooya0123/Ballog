package notfound.ballog.domain.video.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighlightDto {
    private String highlightName;
    private LocalTime startTime;
    private LocalTime endTime;
}
