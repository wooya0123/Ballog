package notfound.ballog.domain.match.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {

    private Integer matchId;

    private String matchName;

    private LocalDate matchDate;

    private LocalTime startTime;

    private LocalTime endTime;

}
