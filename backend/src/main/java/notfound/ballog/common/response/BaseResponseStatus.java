package notfound.ballog.common.response;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    // 성공 응답
    SUCCESS(true, 200, "요청에 성공하였습니다."),

    // 클라이언트 에러 (400번대)
    BAD_REQUEST(false, 400, "잘못된 요청입니다."),
    UNAUTHORIZED(false, 401, "인증이 필요합니다."),
    FORBIDDEN(false, 403, "접근 권한이 없습니다."),
    NOT_FOUND(false, 404, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(false, 405, "허용되지 않는 메서드입니다."),

    // 서버 에러 (500번대)
    INTERNAL_SERVER_ERROR(false, 500, "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(false, 501, "데이터베이스 오류가 발생했습니다."),

    // 비즈니스 로직 관련 에러 (1000번대)
    INVALID_USER_ID(false, 1001, "유효하지 않은 사용자 ID입니다."),
    DUPLICATE_EMAIL(false, 1002, "이미 사용 중인 이메일입니다."),
    PASSWORD_MISMATCH(false, 1003, "비밀번호가 일치하지 않습니다.");
    /*
        이후 자유롭게 에러 추가
     */

    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
