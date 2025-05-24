package notfound.ballog.domain.video.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.video.dto.HighlightDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractHighlightResponse {
    private List<HighlightDto> highlightList;

    public static ExtractHighlightResponse of(List<HighlightDto> highlightList) {
        return ExtractHighlightResponse.builder()
                .highlightList(highlightList)
                .build();
    }
}
