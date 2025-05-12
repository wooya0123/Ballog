package notfound.ballog.domain.video.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.domain.video.dto.HighlightDto;
import notfound.ballog.domain.video.entity.Highlight;
import notfound.ballog.domain.video.entity.Video;
import notfound.ballog.domain.video.repository.HighlightRepository;
import notfound.ballog.domain.video.repository.VideoRepository;
import notfound.ballog.domain.video.request.AddHighlightRequest;
import notfound.ballog.domain.video.request.DeleteHighlightRequest;
import notfound.ballog.domain.video.request.UpdateHighlightRequest;
import notfound.ballog.domain.video.response.AddHighlightResponse;
import notfound.ballog.exception.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HighlightService {

    private final HighlightRepository highlightRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public void updateHighlight(UpdateHighlightRequest request) {
        Highlight highlight = highlightRepository.findById(request.getHighlightId())
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.HIGHLIGHT_NOT_FOUND));
        highlight.update(request);
        highlightRepository.save(highlight);
    }

    @Transactional
    public void deleteHighlight(DeleteHighlightRequest request) {
        Highlight highlight = highlightRepository.findById(request.getHighlightId())
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.HIGHLIGHT_NOT_FOUND));
        highlightRepository.delete(highlight);
    }

    @Transactional
    public AddHighlightResponse addHighlight(AddHighlightRequest request) {
        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.VIDEO_NOT_FOUND));
        Highlight highlight = Highlight.toEntity(video, request.getHighlightName(), request.getStartTime(), request.getEndTime());
        Highlight savedHighlight = highlightRepository.save(highlight);
        return AddHighlightResponse.of(savedHighlight.getHighlightId());
    }
}
