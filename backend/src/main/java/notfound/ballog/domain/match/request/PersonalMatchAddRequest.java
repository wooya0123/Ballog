package notfound.ballog.domain.match.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalMatchAddRequest {

    private LocalDateTime matchDate;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

}
