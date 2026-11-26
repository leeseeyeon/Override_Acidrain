package common.message.lobby;

import common.message.Message;
import common.message.MessageType;
import common.type.Theme;

public class ThemeMessage extends Message {
    private Theme theme;

    // decode할 때 Gson이 쓰는 생성자, gson은 private 생성자도 사용할 수 있음
    private ThemeMessage() {
        super(MessageType.THEME);
    }

    // 방장이 서버로 테마 선택을 보낼 때 쓰는 생성자
    public ThemeMessage(Theme theme) {
        this();
        this.theme = theme;
    }

    public Theme getTheme() {
        return theme;
    }
}