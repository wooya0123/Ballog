package notfound.ballog.domain.video.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.video.dto.HighlightDto;
import notfound.ballog.domain.video.dto.HighlightExtractionDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractHighlightResponse {
    private List<HighlightExtractionDto> highlightList;

    public static ExtractHighlightResponse of(List<HighlightExtractionDto> highlightList) {
        return ExtractHighlightResponse.builder()
                .highlightList(highlightList)
                .build();
    }
}
