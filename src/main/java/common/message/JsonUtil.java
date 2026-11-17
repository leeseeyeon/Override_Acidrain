package common.message;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import common.message.lobby.*;
import common.message.game.*;

public class JsonUtil {
    private static final Gson GSON = new Gson();   // 하나만 만들어서 공유 (thread-safe)

    // type 이름 → 메시지 클래스 대응표 (메시지 추가 시 한 줄씩)
    private static final Map<String, Class<? extends Message>> TYPES = new HashMap<>();
    static {
    	// 대기실 공통
        TYPES.put(MessageType.CHAT,          ChatMessage.class);
        TYPES.put(MessageType.READY,         ReadyMessage.class);
        TYPES.put(MessageType.THEME,         ThemeMessage.class);
        TYPES.put(MessageType.ERROR,         ErrorMessage.class);
        TYPES.put(MessageType.START,         SignalMessage.class);
        TYPES.put(MessageType.OPPONENT_LEFT, SignalMessage.class);

        // 대기실 클라 → 서버
        TYPES.put(MessageType.JOIN,          JoinMessage.class);

        // 대기실 서버 → 클라
        TYPES.put(MessageType.JOINED,        JoinedMessage.class);
        TYPES.put(MessageType.OPPONENT,      OpponentMessage.class);
        TYPES.put(MessageType.ROLE,          RoleMessage.class);

        // GAME_START: 게임 프로토콜 정해지면 추가
        
        // 게임 클라 → 서버
        //TYPES.put(MessageType.WORD_SUBMIT,          .class);
        TYPES.put(MessageType.ITEM_INPUT,    ItemInputMessage.class);
        TYPES.put(MessageType.GAME_QUIT,     SignalMessage.class);
        
        // 게임 서버 → 클라
        TYPES.put(MessageType.GAME_START,    GameStartMessage.class);
        //TYPES.put(MessageType.GAME_STATE_UPDATE,      .class);
        TYPES.put(MessageType.GAME_FINISHED, GameFinishedMessage.class);
    }

    private JsonUtil() {}

    // 보내기: 메시지 객체 → JSON 한 줄
    public static String encode(Message message) {
        return GSON.toJson(message);
    }

    // 받기: JSON 한 줄 → 알맞은 메시지 객체
    public static Message decode(String json) {
        String type = getType(json);
        Class<? extends Message> cls = TYPES.get(type);
        if (cls == null) {
            throw new IllegalArgumentException("알 수 없는 메시지 type: " + type);
        }
        return GSON.fromJson(json, cls);
    }

    // 내부용: 메시지 타입만 꺼내기
    private static String getType(String json) {
        return JsonParser.parseString(json).getAsJsonObject().get("type").getAsString();
    }
}