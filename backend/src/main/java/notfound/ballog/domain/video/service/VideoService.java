package notfound.ballog.domain.video.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.common.utils.S3Util;
import notfound.ballog.domain.match.entity.Match;
import notfound.ballog.domain.match.repository.MatchRepository;
import notfound.ballog.domain.video.dto.HighlightDto;
import notfound.ballog.domain.video.dto.VideoDto;
import notfound.ballog.domain.video.entity.Highlight;
import notfound.ballog.domain.video.entity.Video;
import notfound.ballog.domain.video.repository.HighlightRepository;
import notfound.ballog.domain.video.repository.VideoRepository;
import notfound.ballog.domain.video.request.DeleteVideoRequest;
import notfound.ballog.domain.video.request.AddVideoRequest;
import notfound.ballog.domain.video.response.AddVideoResponse;
import notfound.ballog.domain.video.response.GetVideoListResponse;
import notfound.ballog.exception.NotFoundException;
import notfound.ballog.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final HighlightRepository highlightRepository;
    private final MatchRepository matchRepository;
    private final S3Util s3Util;

    @Transactional
    public AddVideoResponse uploadVideo(AddVideoRequest request) {
        // 업로드한 영상 있는지 확인
        Optional<Video> existingVideo = videoRepository.findByMatch_MatchIdAndQuarterNumber(request.getMatchId(), request.getQuarterNumber());
        if (existingVideo.isPresent()) {
            throw new ValidationException(BaseResponseStatus.VIDEO_ALREADY_EXIST);
        }

        Match match = matchRepository.findById(request.getMatchId())
                        .orElseThrow(() -> new NotFoundException(BaseResponseStatus.MATCH_NOT_FOUND));
        // PresignedUrl 생성
        String objectKey = s3Util.generateObjectKey(request.getFileName(), "video");
        String presignedUrl = s3Util.generatePresignedUrl(objectKey);

        // Duration 타입으로 변환
        String[] part = request.getDuration().split(":");
        long hours = Long.parseLong(part[0]);
        long minutes = Long.parseLong(part[1]);
        long seconds = Long.parseLong(part[2]);
        Duration videoDuration = Duration.ofHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);

        Video video = Video.of(match, request.getQuarterNumber(), presignedUrl, videoDuration);
        Video savedVideo = videoRepository.save(video);
        return AddVideoResponse.of(savedVideo.getVideoUrl());
    }

    @Transactional
    public GetVideoListResponse getVideo(Integer matchId) {
        // 1. 매치 영상 조회
        List<Video> videoList = videoRepository.findAllByMatch_MatchId(matchId);
        // 매치 영상 없으면 예외 처리
        if (videoList.isEmpty()) {
            throw new NotFoundException(BaseResponseStatus.VIDEO_NOT_FOUND);
        }

        List<VideoDto> videoDtoList = new ArrayList<>();
        for (Video video : videoList) {
            Integer videoId = video.getVideoId();
            List<HighlightDto> highlightDtoList = new ArrayList<>();

            // 2. 하이라이트 조회
            List<Highlight> highlightList = highlightRepository.findAllByVideo_VideoId(videoId);
            // 하이라이트가 있으면 리스트에 추가
            for (Highlight highlight : highlightList) {
                HighlightDto highlightDto = HighlightDto.of(highlight);
                highlightDtoList.add(highlightDto);
            }
            // 4. 쿼터 dto 생성
            VideoDto videoDto = VideoDto.of(video, highlightDtoList);

            // 5. 쿼터 리스트 dto에 추가
            videoDtoList.add(videoDto);
        }
        Integer totalQuarters = videoDtoList.size();
        return GetVideoListResponse.of(totalQuarters, videoDtoList);
    }

    @Transactional
    public void deleteVideo(DeleteVideoRequest request) {
        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.VIDEO_NOT_FOUND));
        videoRepository.delete(video);
    }
}
