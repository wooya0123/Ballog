package notfound.ballog.exception;

import lombok.Getter;
import notfound.ballog.common.response.BaseResponseStatus;

@Getter
public class DuplicateEmailException extends RuntimeException {
    private BaseResponseStatus status;

    public DuplicateEmailException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
