package common.message.lobby;

import common.message.Message;
import common.message.MessageType;
import common.type.Role;

public class RoleMessage extends Message {
    private Role role;

    // decode할 때 Gson이 쓰는 생성자, gson은 private 생성자도 사용할 수 있음
    private RoleMessage() {
        super(MessageType.ROLE);
    }

    // 서버가 역할(방장/참가자)을 알려줄 때 쓰는 생성자
    public RoleMessage(Role role) {
        this();
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}