package notfound.ballog.common.exception;

import lombok.Getter;
import notfound.ballog.common.response.BaseResponseStatus;

@Getter
public class InternalServerException extends RuntimeException {
    private BaseResponseStatus status;
    public InternalServerException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
