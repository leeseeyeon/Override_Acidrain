package common.type;

// 서버가 ERROR 메시지로 알려주는 에러 종류
public enum ErrorCode {
    ROOM_NOT_FOUND, // 입장할 방을 찾을 수 없음
    NOT_HOST        // 방장만 할 수 있는 동작(테마 변경, 게임 시작)을 참가자가 요청함
}