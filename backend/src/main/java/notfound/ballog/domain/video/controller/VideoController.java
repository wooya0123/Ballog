package notfound.ballog.domain.video.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.video.request.*;
import notfound.ballog.domain.video.response.AddHighlightResponse;
import notfound.ballog.domain.video.response.AddS3UrlResponse;
import notfound.ballog.domain.video.response.GetVideoListResponse;
import notfound.ballog.domain.video.service.HighlightService;
import notfound.ballog.domain.video.service.VideoService;
import org.springframework.http.MediaType;
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

    @Operation(summary = "s3 presigned url 발급")
    @PostMapping("/url")
    public BaseResponse<AddS3UrlResponse> addS3Url(@RequestBody AddS3UrlRequest request) {
        AddS3UrlResponse response = videoService.addS3Url(request);
        return BaseResponse.ok(response);
    }


    @Operation(summary = "업로드한 영상 저장")
    @PostMapping()
    public BaseResponse<Void> uploadVideo(@Valid @RequestBody AddVideoRequest request) {
        videoService.uploadVideo(request);
        return BaseResponse.ok();
    }


    @Operation(summary = "쿼터 영상 조회")
    @GetMapping("/{matchId}")
    public BaseResponse<GetVideoListResponse> getVideo(
            @PathVariable
            @NotNull(message = "매치 아이디를 입력하세요.")
            Integer matchId
    ) {
        GetVideoListResponse response = videoService.getVideo(matchId);
        return BaseResponse.ok(response);
    }


    @Operation(summary = "쿼터 영상 삭제")
    @DeleteMapping("/{videoId}")
    public BaseResponse<Void> deleteVideo(
            @PathVariable
            @NotNull(message = "영상 아이디를 입력하세요.")
            Integer videoId
    ) {
        videoService.deleteVideo(videoId);
        return BaseResponse.ok();
    }


    @Operation(summary = "하이라이트 자동 추출")
    @PostMapping(value = "/highlight/auto",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BaseResponse<Void> extractHighlight(
            @RequestPart("file") MultipartFile file,
            @RequestPart("videoId") Integer videoId
    ) throws IOException {
        System.out.println("비디오 아이디 -------------------------------------" +videoId);

        highlightService.extractHighlight(videoId, file);
        return BaseResponse.ok();
    }


    @Operation(summary = "하이라이트 구간 수정")
    @PatchMapping("/highlight")
    public BaseResponse<Void> updateHighlight(@Valid @RequestBody UpdateHighlightRequest request) {
        highlightService.updateHighlight(request);
        return BaseResponse.ok();
    }


    @Operation(summary = "하이라이트 구간 삭제")
    @DeleteMapping("/highlight/{highlightId}")
    public BaseResponse<Void> deleteHighlight(
            @NotNull(message = "하이라이트 아이디를 입력하세요.")
            @PathVariable Integer highlightId
    ){
        highlightService.deleteHighlight(highlightId);
        return BaseResponse.ok();
    }


    @Operation(summary = "하이라이트 구간 추가")
    @PostMapping("/highlight")
    public BaseResponse<AddHighlightResponse> addHighlight(@Valid @RequestBody AddHighlightRequest request) {
        AddHighlightResponse response = highlightService.addHighlight(request);
        return BaseResponse.ok(response);
    }

}
