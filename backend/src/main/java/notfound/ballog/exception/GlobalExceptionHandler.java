package notfound.ballog.exception;

import lombok.extern.slf4j.Slf4j;
import notfound.ballog.common.response.BaseResponse;
import notfound.ballog.common.response.BaseResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(DuplicateDataException.class)
    public BaseResponse<BaseResponseStatus> DuplicateDataExceptionHandler(DuplicateDataException e) {
        log.error("DuplicateDataException {} {} {}", e.getMessage(), e.getCause(), e.getStackTrace() );
        return BaseResponse.error(e.getStatus());
    }

    @ExceptionHandler(ValidationException.class)
    public BaseResponse<BaseResponseStatus> ValidationExceptionHandler(ValidationException e) {
        log.error("ValidationException {} {} {}", e.getMessage(), e.getCause(), e.getStackTrace() );
        return BaseResponse.error(e.getStatus());
    }

    // Valid 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.error("MethodArgumentNotValidException {}", message );
        BaseResponse<Void> body = BaseResponse.error(BaseResponseStatus.BAD_REQUEST, message);
        return ResponseEntity.ok(body);
    }
}
