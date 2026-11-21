package server.game;

public class Theme {

    private int themeId;
    private String themeName;

    // 생성자
    public Theme(int themeId, String themeName) {
        this.themeId = themeId;
        this.themeName = themeName;
    }

    // 테마 ID 반환
    public int getThemeId() {
        return themeId;
    }

    // 테마 이름 반환
    public String getThemeName() {
        return themeName;
    }
}