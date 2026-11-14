package common.message;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import common.message.lobby.*;

public class JsonUtil {
    private static final Gson GSON = new Gson();   // 하나만 만들어서 공유 (thread-safe)

    // type 이름 → 메시지 클래스 대응표 (메시지 추가 시 한 줄씩)
    private static final Map<String, Class<? extends Message>> TYPES = new HashMap<>();
    static {
        //TYPES.put(MessageType.CHAT,   ChatMessage.class);
        //TYPES.put(MessageType.JOINED, JoinedMessage.class);
        //TYPES.put(MessageType.READY,  ReadyMessage.class);
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