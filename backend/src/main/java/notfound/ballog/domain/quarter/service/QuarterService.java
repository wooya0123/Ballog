package notfound.ballog.domain.quarter.service;

import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.match.repository.MatchRepository;
import notfound.ballog.domain.quarter.dto.ReportData;
import notfound.ballog.domain.quarter.entity.Quarter;
import notfound.ballog.domain.quarter.repository.QuarterRepository;
import notfound.ballog.domain.quarter.request.AddQuarterAndGameReportRequest;
import notfound.ballog.exception.NotFoundException;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    @Transactional
    public void addQuarterAndGameReport(UUID userId, AddQuarterAndGameReportRequest req){
        Integer matchId = matchRepository.findMatchIdByUserIdAndMatchDate(userId, req.getMatchDate());

        if(matchId == null){
            // 원래 여기서 매치 추가하고 그 매치 아이디로 쿼터 등록하고 경기 기록 등록 (자동 등록 버전)
            throw new NotFoundException(BaseResponseStatus.BAD_REQUEST);
        }

        // matchId에 해당하는 모든 Quarter를 한 번에 조회
        List<Quarter> existingQuarters = quarterRepository.findAllByMatchId(matchId);
        Map<Integer, Quarter> quarterNumberToQuarter = existingQuarters.stream()
                .collect(Collectors.toMap(Quarter::getQuarterNumber, quarter -> quarter));

        // ReportData 리스트를 순회하며 필요한 Quarter ID 목록 생성
        List<Integer> quarterIds = new ArrayList<>();
        List<Quarter> quartersToSave = new ArrayList<>();

        for (ReportData reportData : req.getReportDataList()) {
            int quarterNumber = reportData.getQuarterNumber();
            if (quarterNumberToQuarter.containsKey(quarterNumber)) {
                // 이미 존재하면 ID 추가
                quarterIds.add(quarterNumberToQuarter.get(quarterNumber).getQuarterId());
            } else {
                // 없으면 저장할 목록에 추가
                Quarter newQuarter = new Quarter(matchId, quarterNumber);
                quartersToSave.add(newQuarter);
            }
        }

        // 없는 Quarter들을 한 번에 저장
        if (!quartersToSave.isEmpty()) {
            List<Quarter> savedQuarters = quarterRepository.saveAll(quartersToSave);
            // 저장된 Quarter들의 ID를 결과 목록에 추가
            quarterIds.addAll(savedQuarters.stream()
                    .map(Quarter::getQuarterId)
                    .toList());
        }

    }

}
