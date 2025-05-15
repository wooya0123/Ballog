package notfound.ballog.domain.video.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.video.dto.HighlightDto;
import notfound.ballog.domain.video.entity.Highlight;
import notfound.ballog.domain.video.entity.Video;
import notfound.ballog.domain.video.repository.HighlightRepository;
import notfound.ballog.domain.video.repository.VideoRepository;
import notfound.ballog.domain.video.request.AddHighlightRequest;
import notfound.ballog.domain.video.request.UpdateHighlightRequest;
import notfound.ballog.domain.video.response.AddHighlightResponse;
import notfound.ballog.domain.video.response.ExtractHighlightResponse;
import notfound.ballog.exception.DuplicateDataException;
import notfound.ballog.exception.NotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighlightService {

    private final HighlightRepository highlightRepository;
    private final VideoRepository videoRepository;
    private final WebClient webClient;

    @Transactional
    public void updateHighlight(UpdateHighlightRequest request) {
        Highlight highlight = highlightRepository.findById(request.getHighlightId())
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.HIGHLIGHT_NOT_FOUND));
        highlight.update(request);
        highlightRepository.save(highlight);
    }

    @Transactional
    public void deleteHighlight(Integer highlightId) {
        Highlight highlight = highlightRepository.findById(highlightId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.HIGHLIGHT_NOT_FOUND));
        highlight.delete();
        highlightRepository.save(highlight);
    }

    @Transactional
    public AddHighlightResponse addHighlight(AddHighlightRequest request) {
        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.VIDEO_NOT_FOUND));
        Highlight highlight = Highlight.toEntity(video, request.getHighlightName(), request.getStartTime(), request.getEndTime());
        Highlight savedHighlight = highlightRepository.save(highlight);
        return AddHighlightResponse.of(savedHighlight.getHighlightId());
    }

    @Transactional
    public void extractHighlight(Integer videoId, MultipartFile file) throws IOException {
        // Video 조회
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.VIDEO_NOT_FOUND));

        // Highlight 조회(Highlight가 하나도 없을 때만 자동 추출)
        Optional<Highlight> existingHighlight = highlightRepository.findByVideo_VideoIdAndDeletedFalse(videoId);
        if (existingHighlight.isPresent()) {
            throw new DuplicateDataException(BaseResponseStatus.HIGHLIGHT_ALREADY_EXIST);
        }

        // 파일을 담은 form-data 생성
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file",
                new ByteArrayResource(file.getBytes()) {
                    @Override public String getFilename() {
                        return file.getOriginalFilename();
                    }
                })
                .header("Content-Type", file.getContentType());

        // fastAPI로 post 요청
        ExtractHighlightResponse highlightResponse = webClient.post()
                .uri("/api/v1/videos/highlight/extract")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ExtractHighlightResponse.class)
                .block();

        // DB에 하이라이트 저장
        List<HighlightDto> highlightList = null;
        if (highlightResponse != null) {
            highlightList = highlightResponse.getHighlightList();
        }
        log.info("하이라이트 리스트--------------- {}", highlightList);

        if (highlightList != null) {
            log.info("하이라이트 갯수--------------- {}", highlightList.size());
        }

        if (highlightList != null) {
            for (HighlightDto highlightDto : highlightList) {
                log.info("하이라이트 ----------- {}", highlightDto);

                Highlight highlight = Highlight.of(video, highlightDto);
                highlightRepository.save(highlight);
            }
        }
    }
}
