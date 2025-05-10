package notfound.ballog.domain.video.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuarterListDto {
    private List<QuarterDto> quaterList;

    public void add(QuarterDto quarterDto) {
        this.quaterList.add(quarterDto);
    }
}
