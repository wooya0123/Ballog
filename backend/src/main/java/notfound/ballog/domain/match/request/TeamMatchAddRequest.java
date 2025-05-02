package notfound.ballog.domain.match.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMatchAddRequest {

    private Integer teamId;

    private LocalDateTime matchDate;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private List<Integer> participantList;

}
