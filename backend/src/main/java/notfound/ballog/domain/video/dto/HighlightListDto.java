package notfound.ballog.domain.video.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighlightListDto {
    private List<HighlightDto> highlightList;
}
