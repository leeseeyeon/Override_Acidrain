package common.message.game;

public class PlayerProfile {
    private int playerId;
    private String nickname;
    
    // 두 플레이어가 모두 준비를 완료하여 게임이 시작될 때 클라와 서버 둘 다 쓰는 클래스 (대기실의 playerInfo - 게임 중의 playerProfile)

    public PlayerProfile(int playerId, String nickname) {
        this.playerId = playerId;
        this.nickname = nickname;
    }

    public int getPlayerId() {
    	return playerId;
    }
    
    public String getNickname() {
    	return nickname;
    }
}