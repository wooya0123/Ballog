package notfound.ballog.domain.match.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {

    private Integer matchId;

    private String location;

    private LocalDateTime matchDate;

    private LocalTime startTime;

    private LocalTime endTime;

}
