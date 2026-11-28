package common.message;

import common.type.ErrorCode;

public class ErrorMessage extends Message {
    private ErrorCode errorCode;

    // decode할 때 Gson이 쓰는 생성자, gson은 private 생성자도 사용할 수 있음
    private ErrorMessage() {
        super(MessageType.ERROR);
    }

    // 서버가 오류 사유를 보낼 때 쓰는 생성자
    public ErrorMessage(ErrorCode errorCode) {
        this();
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}