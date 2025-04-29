package notfound.ballog.common.exception;

import lombok.extern.slf4j.Slf4j;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.common.response.BaseResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InternalServerException.class)
    public BaseResponse<BaseResponseStatus> InternalServerExceptionHandler(InternalServerException e) {
        log.error("InternalServerException {} {} {}", e.getMessage(), e.getCause(), e.getStackTrace() );
        return BaseResponse.error(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }
}
