package notfound.ballog.domain.video.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.match.entity.Match;
import notfound.ballog.domain.match.repository.MatchRepository;
import notfound.ballog.domain.video.dto.HighlightDto;
import notfound.ballog.domain.video.dto.HighlightListDto;
import notfound.ballog.domain.video.dto.QuarterDto;
import notfound.ballog.domain.video.dto.QuarterListDto;
import notfound.ballog.domain.video.entity.Highlight;
import notfound.ballog.domain.video.entity.Video;
import notfound.ballog.domain.video.repository.HighlightRepository;
import notfound.ballog.domain.video.repository.VideoRepository;
import notfound.ballog.domain.video.request.DeleteVideoRequest;
import notfound.ballog.domain.video.request.UploadVideoRequest;
import notfound.ballog.domain.video.response.GetVideoListResponse;
import notfound.ballog.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final MatchRepository matchRepository;
    private final HighlightRepository highlightRepository;

    @Transactional
    public void uploadVideo(UploadVideoRequest request) {
//        String videoUrl = request.getVideoUrl();
//
//        // 1. Video 저장
//        Match match = matchRepository.findById(request.getMatchId())
//                        .orElseThrow(() -> new NotFoundException(BaseResponseStatus.MATCH_NOT_FOUND));
//
//        String[] part = request.getDuration().split(":");
//        long hours = Long.parseLong(part[0]);
//        long minutes = Long.parseLong(part[1]);
//        long seconds = Long.parseLong(part[2]);
//        Duration videoDuration = Duration.ofHours(hours)
//                                        .plusMinutes(minutes)
//                                        .plusSeconds(seconds);
//
//        Video video = Video.of(match, request.getQuaterNumber(), request.getVideoUrl(), videoDuration);
//        Video savedVideo = videoRepository.save(video);
//
//        // 2. fastapi 호출(하이라이트 추출)
//
//        HighlightListDto highlightListDto = new HighlightListDto();     // 응답 받은 하이라이트 리스트
//
//        // 3. 하이라이트 저장(배치 저장)
//        List<Highlight> entities = highlightListDto.getHighlightList().stream()
//                .map(highlightDto -> Highlight.of(savedVideo, highlightDto))      // DTO → 엔티티 매핑
//                .collect(Collectors.toList());                                               // List<Highlight> 로 수집
//        // 한 번에 모두 저장
//        highlightRepository.saveAll(entities);
    }

    @Transactional
    public GetVideoListResponse getVideo(Integer matchId) {
        // 1. 매치 영상 조회
        List<Video> videoList = videoRepository.findAllByMatchId(matchId);
        // 매치 영상 없으면 예외 처리
        if (videoList.isEmpty()) {
            throw new NotFoundException(BaseResponseStatus.VIDEO_NOT_FOUND);
        }

        QuarterListDto quarterListDto = new QuarterListDto();
        // 2. 하이라이트 조회
        for (Video video : videoList) {
            Integer videoId = video.getVideoId();
            List<Highlight> highlightList = highlightRepository.findAllByVideoId(videoId);
            // 하이라이트 없으면 예외 처리
            if (highlightList.isEmpty()) {
                throw new NotFoundException(BaseResponseStatus.HIGHLIGHT_NOT_FOUND);
            }
            // 3. 하이라이트 리스트 dto 생성
            HighlightListDto highlightListDto = new HighlightListDto();
            for (Highlight highlight : highlightList) {
                HighlightDto highlightDto = HighlightDto.of(highlight);
                highlightListDto.add(highlightDto);
            }
            // 4. 쿼터 dto 생성
            QuarterDto quarterDto = QuarterDto.of(video, highlightListDto);

            // 5. 쿼터 리스트 dto에 추가
            quarterListDto.add(quarterDto);
        }
        Integer totalQuarters = quarterListDto.getQuaterList().size();
        return GetVideoListResponse.of(totalQuarters, quarterListDto);
    }

    @Transactional
    public void deleteVideo(DeleteVideoRequest request) {
        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.VIDEO_NOT_FOUND));
        videoRepository.delete(video);
    }
}
