package common.type;

public enum Theme {
    BASIC(1, "기본"),
    CS(2, "컴퓨터 용어"),
	ENGLISH(3, "영단어");

    private final int id;           	// DB 조회용 (채원님)
    private final String displayName;   // 화면 표시용 (윤영·채영님)

    Theme(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    
    public int getId() {
    	return id;
    }
    
    public String getDisplayName() {
    	return displayName;
    }
}
