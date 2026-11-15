package common.message.lobby;

import common.message.Message;
import common.message.MessageType;

public class OpponentMessage extends Message {
    private String nickname;

    // decode할 때 Gson이 쓰는 생성자, gson은 private 생성자도 사용할 수 있음
    private OpponentMessage() {
        super(MessageType.OPPONENT);
    }

    // 서버가 상대방 닉네임을 알려줄 때 쓰는 생성자
    public OpponentMessage(String nickname) {
        this();
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}