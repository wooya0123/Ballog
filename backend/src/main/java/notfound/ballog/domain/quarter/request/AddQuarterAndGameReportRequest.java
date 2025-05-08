package notfound.ballog.domain.quarter.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import notfound.ballog.domain.quarter.dto.ReportData;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddQuarterAndGameReportRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate matchDate;

    private List<ReportData> reportDataList;

}
