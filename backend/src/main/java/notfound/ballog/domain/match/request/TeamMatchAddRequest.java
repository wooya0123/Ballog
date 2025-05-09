package notfound.ballog.domain.match.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMatchAddRequest {

    private Integer teamId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate matchDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String matchName;

    private List<Integer> participantList;

}
