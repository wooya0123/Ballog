package notfound.ballog.domain.video.dto;

import lombok.*;
import notfound.ballog.domain.video.entity.Video;

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

    public static QuarterDto of(Video video, HighlightListDto highlightListDto) {
        return QuarterDto.builder()
                .videoId(video.getVideoId())
                .quarterNumber(video.getQuaterNumber())
                .videoUrl(video.getVideoUrl())
                .highlightList(highlightListDto)
                .build();
    }
}
