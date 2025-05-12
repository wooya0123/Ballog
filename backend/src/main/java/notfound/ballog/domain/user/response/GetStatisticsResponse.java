package notfound.ballog.domain.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetStatisticsResponse {
    private String nickname;
    private List<List<List<Integer>>> heatmap;
    private List<Double> distance;
    private List<Integer> speed;
    private List<Integer> sprint;
    private List<Integer> heartRate;

    public static GetStatisticsResponse of(String nickname,
                                           List<List<List<Integer>>> heatmap,
                                           List<Double> distance,
                                           List<Integer> speed,
                                           List<Integer> sprint,
                                           List<Integer> heartRate) {
        return GetStatisticsResponse.builder()
                .nickname(nickname)
                .heatmap(heatmap)
                .distance(distance)
                .speed(speed)
                .sprint(sprint)
                .heartRate(heartRate)
                .build();
    }
}
