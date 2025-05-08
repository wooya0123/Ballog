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

    // 회원 로직 관련 에러 (1000번대)
    INVALID_EMAIL(false, 1001, "유효하지 않은 이메일입니다."),
    DUPLICATE_EMAIL(false, 1002, "이미 사용 중인 이메일입니다."),
    PASSWORD_MISMATCH(false, 1003, "비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(false, 1004, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(false, 1005, "토큰이 만료되었습니다."),
    MISSING_TOKEN(false, 1006, "요청에 토큰이 존재하지 않습니다."),
    EMAIL_AUTH_CODE_SEND_FAIL(false, 1007, "이메일 확인 코드 전송 중 오류가 발생했습니다."),
    EXPIRED_EMAIL_AUTH_CODE(false, 1008, "이메일 인증 코드가 없거나 만료되었습니다."),
    INVALID_EMAIL_AUTH_CODE(false, 1009, "유효하지 않은 이메일 인증 코드입니다."),

    // 유저 관련 로직 에러 (2000번대)
    USER_NOT_FOUND(false, 2000, "해당하는 사용자가 존재하지 않습니다."),
    PLAYERCARD_NOT_FOUND(false, 2001, "해당하는 사용자의 선수카드가 존재하지 않습니다."),
    USER_INACTIVE(false, 2002, "탈퇴한 사용자입니다."),

    // 매치 관련 로직 에러(3000번대)
    MATCH_NOT_FOUND(false, 3001, "해당하는 매치가 존재하지 않습니다.");

    // 팀 관련 로직 에러 (3000)

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
