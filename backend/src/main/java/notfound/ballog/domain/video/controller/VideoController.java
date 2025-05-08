package notfound.ballog.domain.video.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.domain.video.request.UploadVideoRequest;
import notfound.ballog.domain.video.service.VideoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping()
    public BaseResponse<Void> uploadVideo(@Valid @RequestBody UploadVideoRequest request) {
        videoService.uploadVideo(request);
        return BaseResponse.ok();
    }
}
