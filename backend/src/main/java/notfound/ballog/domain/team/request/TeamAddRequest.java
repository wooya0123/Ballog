package notfound.ballog.domain.team.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamAddRequest {

    private String teamName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate foundationDate;

    private String logoImageUrl;

}
