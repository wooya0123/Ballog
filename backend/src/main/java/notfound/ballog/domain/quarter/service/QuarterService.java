package notfound.ballog.domain.quarter.service;

import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.match.repository.MatchRepository;
import notfound.ballog.domain.quarter.dto.ReportData;
import notfound.ballog.domain.quarter.entity.GameReport;
import notfound.ballog.domain.quarter.entity.Quarter;
import notfound.ballog.domain.quarter.repository.GameReportRepository;
import notfound.ballog.domain.quarter.repository.QuarterRepository;
import notfound.ballog.domain.quarter.request.AddQuarterAndGameReportRequest;
import notfound.ballog.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuarterService {

    private final QuarterRepository quarterRepository;
    private final MatchRepository matchRepository;
    private final GameReportRepository gameReportRepository;

    @Transactional
    public void addQuarterAndGameReport(UUID userId, AddQuarterAndGameReportRequest req){
        Integer matchId = matchRepository.findMatchIdByUserIdAndMatchDate(userId, req.getMatchDate());

        if(matchId == null){
            // 원래 여기서 매치 추가하고 그 매치 아이디로 쿼터 등록하고 경기 기록 등록 (자동 등록 버전)
            throw new NotFoundException(BaseResponseStatus.BAD_REQUEST);
        }

        List<Quarter> existingQuarters = quarterRepository.findAllByMatchId(matchId);

        Map<Integer, Quarter> quarterNumberToQuarter = existingQuarters.stream()
                .collect(Collectors.toMap(Quarter::getQuarterNumber, quarter -> quarter));

        List<Integer> quarterIds = new ArrayList<>();
        List<Quarter> quartersToSave = new ArrayList<>();

        for (ReportData reportData : req.getReportDataList()) {
            int quarterNumber = reportData.getQuarterNumber();

            if (quarterNumberToQuarter.containsKey(quarterNumber)) {
                quarterIds.add(quarterNumberToQuarter.get(quarterNumber).getQuarterId());
            } else {
                Quarter newQuarter = new Quarter(matchId, quarterNumber);
                quartersToSave.add(newQuarter);
            }
        }

        if (!quartersToSave.isEmpty()) {
            quarterRepository.saveAll(quartersToSave);
        }

        List<Integer> requestedQuarterNumbers = req.getReportDataList().stream()
                .map(ReportData::getQuarterNumber)
                .collect(Collectors.toList());

        List<Quarter> requestedQuarters = quarterRepository.findAllByMatchIdAndQuarterNumberIn(
                matchId, requestedQuarterNumbers);

        Map<Integer, Quarter> quarterMap = requestedQuarters.stream()
                .collect(Collectors.toMap(Quarter::getQuarterNumber, quarter -> quarter));

        List<GameReport> gameReportsToSave = new ArrayList<>();
        for (ReportData reportData : req.getReportDataList()) {
            Quarter quarter = quarterMap.get(reportData.getQuarterNumber());

            if (quarter != null) {
                gameReportsToSave.add(new GameReport(userId, quarter.getQuarterId(), reportData.getGameReportData()));
            }
        }

        // 생성된 모든 GameRecord 저장
        if (!gameReportsToSave.isEmpty()) {
            gameReportRepository.saveAll(gameReportsToSave);
        }

        //선수카드 업데이트

    }

}
