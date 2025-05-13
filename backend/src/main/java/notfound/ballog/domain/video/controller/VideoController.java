package notfound.ballog.domain.video.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.video.request.*;
import notfound.ballog.domain.video.response.AddHighlightResponse;
import notfound.ballog.domain.video.response.AddVideoResponse;
import notfound.ballog.domain.video.response.GetVideoListResponse;
import notfound.ballog.domain.video.service.HighlightService;
import notfound.ballog.domain.video.service.VideoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(
        name = "Video"
)
@RestController
@RequestMapping("/v1/videos")
@RequiredArgsConstructor
@Validated
public class VideoController {

    private final VideoService videoService;
    private final HighlightService highlightService;

    @Operation(
            summary = "영상 업로드"
    )
    @PostMapping()
    public BaseResponse<AddVideoResponse> uploadVideo(@Valid @RequestBody AddVideoRequest request) {
        AddVideoResponse response = videoService.uploadVideo(request);
        return BaseResponse.ok(response);
    }

    @Operation(
            summary = "영상 업로드 체크"
    )
    @PostMapping("/status")
    public BaseResponse<Void> updateVideo(@Valid @RequestBody UpdateVideoRequest request) {
        videoService.updateVideo(request);
        return BaseResponse.ok();
    }

    @Operation(
            summary = "매치 영상 조회"
    )
    @GetMapping("/{matchId}")
    public BaseResponse<GetVideoListResponse> getVideo(
            @PathVariable
            @NotNull(message = "매치 아이디를 입력하세요.")
            Integer matchId
    ) {
        GetVideoListResponse response = videoService.getVideo(matchId);
        return BaseResponse.ok(response);
    }

    @Operation(
            summary = "매치 영상 삭제"
    )
    @DeleteMapping()
    public BaseResponse<Void> deleteVideo(@Valid @RequestBody DeleteVideoRequest request) {
        videoService.deleteVideo(request);
        return BaseResponse.ok();
    }

    @PostMapping("/highlight/auto")
    public BaseResponse<Void> extractHighlight(
            @RequestPart("json") ExtractHighlightRequest request,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        highlightService.extractHighlight(request, file);
        return BaseResponse.ok();
    }

    @Operation(
            summary = "하이라이트 구간 수정"
    )
    @PatchMapping("/highlight")
    public BaseResponse<Void> updateHighlight(@Valid @RequestBody UpdateHighlightRequest request) {
        highlightService.updateHighlight(request);
        return BaseResponse.ok();
    }

    @Operation(
            summary = "하이라이트 구간 삭제"
    )
    @DeleteMapping("/highlight")
    public BaseResponse<Void> deleteHighlight(@Valid @RequestBody DeleteHighlightRequest request) {
        highlightService.deleteHighlight(request);
        return BaseResponse.ok();
    }

    @Operation(
            summary = "하이라이트 구간 추가"
    )
    @PostMapping("/highlight")
    public BaseResponse<AddHighlightResponse> addHighlight(@Valid @RequestBody AddHighlightRequest request) {
        AddHighlightResponse response = highlightService.addHighlight(request);
        return BaseResponse.ok(response);
    }

}
