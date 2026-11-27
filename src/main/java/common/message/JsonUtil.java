package common.message;


import com.google.gson.Gson;
import com.google.gson.JsonParser;


public class JsonUtil {
    private static final Gson GSON = new Gson();   // 하나만 만들어서 공유 (thread-safe)

    private JsonUtil() {}

    // 보내기: 메시지 객체 → JSON 한 줄
    public static String encode(Message message) {
        return GSON.toJson(message);
    }

    // 받기: JSON 한 줄 → 알맞은 메시지 객체
    public static Message decode(String json) {
        MessageType type = MessageType.valueOf(getType(json)); // 모르는 type이면 IllegalArgumentException
        return GSON.fromJson(json, type.getMessageClass());
    }
    
    // 해석 못 하는 메시지(등록 안 된 type, 깨진 JSON 등)면 로그 남기고 null 반환
    // caller: 로그에 찍힐 호출한 쪽 이름 (예: "LobbyController")
    public static Message decodeOrNull(String json, String caller) {
        try {
            return decode(json);
        } catch (RuntimeException e) {
            System.err.println("[" + caller + "] 해석할 수 없는 메시지: " + json);
            return null;
        }
    }

    // 내부용: 메시지 타입만 꺼내기
    private static String getType(String json) {
        return JsonParser.parseString(json).getAsJsonObject().get("type").getAsString();
    }
}