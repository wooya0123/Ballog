package notfound.ballog.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.auth.entity.Auth;
import notfound.ballog.domain.auth.repository.AuthRepository;
import notfound.ballog.domain.quarter.entity.GameReport;
import notfound.ballog.domain.quarter.repository.GameReportRepository;
import notfound.ballog.domain.user.entity.User;
import notfound.ballog.domain.user.repository.UserRepository;
import notfound.ballog.domain.user.request.UpdateUserRequest;
import notfound.ballog.domain.user.response.GetStatisticsResponse;
import notfound.ballog.domain.user.response.GetUserResponse;
import notfound.ballog.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final GameReportRepository gameReportRepository;
    private final PlayerCardService playerCardService;
//    private final OpenAIService openAIService;

    // 회원가입
    @Transactional
    public User signUp(User newUser) {
        // 유저 저장
        User savedUser = userRepository.save(newUser);

        // 플레이어 카드 생성
        playerCardService.addPlayerCard(savedUser);

        return savedUser;
    }


    public GetUserResponse getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));
        Auth auth = authRepository.findByUser_UserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));
        String email = auth.getEmail();
        return GetUserResponse.of(user, email);
    }


    @Transactional
    public void updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));
        user.updateUser(request);
    }


    @Transactional
    public User reactivateUser(User user) {
        return userRepository.save(user);
    }

    public GetStatisticsResponse getStatistics(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));

        // 1. 최대 5개의 쿼터 리포트 조회
        List<GameReport> gameReportList =
                gameReportRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);

        // 2. 응답에 담을 각 필드별 리스트 초기화
        List<List<List<Integer>>> heatmapList = new ArrayList<>(); // 2D 배열 5개
        List<Double> distanceList       = new ArrayList<>();     // 거리 5개
        List<Double> speedList         = new ArrayList<>();     // 평균 속도 5개
        List<Integer> sprintList        = new ArrayList<>();     // 스프린트 횟수 5개
        List<Integer> heartRateList     = new ArrayList<>();     // 평균 심박수 5개

        // 3. gameReport에서 값 꺼내서 담아주기
        for (GameReport gameReport : gameReportList) {
            Map<String, Object> reportData = (Map<String, Object>) gameReport.getReportData();

            List<List<Integer>> heatmap = (List<List<Integer>>) reportData.get("heatmap");
            heatmapList.add(heatmap);

            Number distance = (Number) reportData.get("distance");
            distanceList.add(distance.doubleValue());

            Number speed = (Number) reportData.get("avgSpeed");
            speedList.add(speed.doubleValue());

            Number sprint = (Number) reportData.get("sprint");
            sprintList.add(sprint.intValue());

            Number heartRate = (Number) reportData.get("avgHeartRate");
            heartRateList.add(heartRate.intValue());
        }

        // 4. 히트맵 평균 내기
        List<List<Integer>> averagedHeatmap = new ArrayList<>();

        if (!heatmapList.isEmpty()) {
            // 2) 첫 번째 heatmap의 사이즈를 기준으로 행(row)과 열(col) 크기 결정
            int numRows = heatmapList.get(0).size();
            int numCols = heatmapList.get(0).get(0).size();
            int count = heatmapList.size();

            // 3) 행 단위 순회
            for (int i = 0; i < numRows; i++) {
                List<Integer> averagedRow = new ArrayList<>();

                // 4) 열 단위 순회
                for (int j = 0; j < numCols; j++) {
                    double sum = 0.0;

                    // 5) 5개의 heatmap에서 같은 (i,j) 위치 값을 모두 더하기
                    for (List<List<Integer>> singleHeatmap : heatmapList) {
                        // 만약 일부 heatmap에 해당 위치가 없으면 0 처리
                        List<Integer> row = singleHeatmap.get(i);
                        int value = (j < row.size() ? row.get(j) : 0);
                        sum += value;
                    }

                    // 6) 평균 계산 및 소수점 저장
                    int avgInt = (int) Math.round(sum / count);
                    averagedRow.add(avgInt);
                }

                // 7) 완성된 행을 결과에 추가
                averagedHeatmap.add(averagedRow);
            }
        }

        return GetStatisticsResponse.of(user.getNickname(),
                                        averagedHeatmap,
                                        distanceList,
                                        speedList,
                                        sprintList,
                                        heartRateList);
    }

//     // 선수 추천 기능
//    public String getRecommendedPlayer(UUID userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.USER_NOT_FOUND));
//
//        // 최대 5개의 쿼터 리포트 조회
//        List<GameReport> gameReportList =
//                gameReportRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
//
//        if (gameReportList.isEmpty()) {
//            throw new NotFoundException(BaseResponseStatus.MATCH_NOT_FOUND);
//        }
//
//        // 게임 리포트 데이터를 JSON 형태로 변환
//        List<Map<String, Object>> gameDataList = new ArrayList<>();
//        for (GameReport gameReport : gameReportList) {
//            gameDataList.add((Map<String, Object>) gameReport.getReportData());
//        }
//
//        // GPT 프롬프트 구성
//        String prompt = "다음은 풋살 경기에서 얻은 5개의 게임 데이터입니다:\n\n" +
//                gameDataList.toString() +
//                "\n\n이 데이터를 바탕으로 비슷한 능력치를 가진 프로축구선수를 추천해주세요. " +
//                "각 데이터의 sprint는 스프린트 횟수, avgSpeed는 평균 속도(km/h), " +
//                "distance는 이동 거리(m), avgHeartRate는 평균 심박수, " +
//                "heatmap은 경기장에서의 위치 히트맵입니다.";
//
//        // OpenAI API 호출하여 응답 받기
//        String recommendation = openAIService.getCompletionFromGPT(prompt);
//
//        return recommendation;
//    }
    
}

