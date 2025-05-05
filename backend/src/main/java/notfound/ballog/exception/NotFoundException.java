package notfound.ballog.exception;

import lombok.Getter;
import notfound.ballog.common.response.BaseResponseStatus;

@Getter
public class NotFoundException extends RuntimeException {
    private BaseResponseStatus status;

    public NotFoundException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
