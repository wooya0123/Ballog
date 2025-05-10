package notfound.ballog.domain.video.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.video.request.*;
import notfound.ballog.domain.video.response.AddHighlightResponse;
import notfound.ballog.domain.video.response.GetVideoListResponse;
import notfound.ballog.domain.video.service.HighlightService;
import notfound.ballog.domain.video.service.VideoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/videos")
@RequiredArgsConstructor
@Validated
public class VideoController {

    private final VideoService videoService;
    private final HighlightService highlightService;

    @PostMapping()
    public BaseResponse<Void> uploadVideo(@Valid @RequestBody UploadVideoRequest request) {
        videoService.uploadVideo(request);
        return BaseResponse.ok();
    }

    @GetMapping("/{matchId}")
    public BaseResponse<GetVideoListResponse> getVideo(
            @PathVariable
            @NotNull(message = "매치 아이디를 입력하세요.")
            Integer matchId
    ) {
        GetVideoListResponse response = videoService.getVideo(matchId);
        return BaseResponse.ok(response);
    }

    @DeleteMapping()
    public BaseResponse<Void> deleteVideo(@Valid @RequestBody DeleteVideoRequest request) {
        videoService.deleteVideo(request);
        return BaseResponse.ok();
    }

    @PatchMapping("/highlight")
    public BaseResponse<Void> updateHighlight(@Valid @RequestBody UpdateHighlightRequest request) {
        highlightService.updateHighlight(request);
        return BaseResponse.ok();
    }

    @DeleteMapping("/highlight")
    public BaseResponse<Void> deleteHighlight(@Valid @RequestBody DeleteHighlightRequest request) {
        highlightService.deleteHighlight(request);
        return BaseResponse.ok();
    }

    @PostMapping("/highlight")
    public BaseResponse<AddHighlightResponse> addHighlight(@Valid @RequestBody AddHighlightRequest request) {
        AddHighlightResponse response = highlightService.addHighlight(request);
        return BaseResponse.ok(response);
    }

}
