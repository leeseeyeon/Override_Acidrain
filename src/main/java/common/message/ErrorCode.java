package common.message;

public enum ErrorCode {
	// 대기실용 에러코드
    ROOM_NOT_FOUND,
    NOT_HOST,
    
    // 게임 중 에러코드
    NOT_IN_GAME,
    GAME_ALREADY_FINISHED,
    INVALID_ITEM,
    
    // 공통 에러코드
    INVALID_MESSAGE
}
