package client.start;

import java.util.regex.*;

/**
 * 닉네임 규칙 
 * 1. 닉네임을 반드시 입력해야함.
 * 2. 공백, 탭 불가 (앞, 뒤, 가운데 모두)
 * 3. 한글, 영문, 숫자만 허용/ 기호 불가
 * 4. 최대 10자까지 허용
 */
public class NicknameValidator {
    public static final int MAX_NICKNAME_LENGTH = 10;

    private static final String EMPTY_WARNING = "닉네임을 입력하세요.";
    private static final String SPACE_WARNING = "닉네임에 띄어쓰기를 쓸 수 없습니다.";
    private static final String SYMBOL_WARNING = "닉네임에는 한글, 영문, 숫자만 쓸 수 있습니다.";
    private static final String LENGTH_WARNING = "닉네임은 " + MAX_NICKNAME_LENGTH + "자 이하로 입력하세요.";

    /** 한글 완성형(가~힣), 자음(ㄱ~ㅎ), 모음(ㅏ~ㅣ), 영문 대소문자, 숫자만 허용*/
    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]+");

    public boolean isValidNickname(String nickname) {
        return findWarningMessage(nickname) == null;
    }
    
    private boolean containsWhitespace(String nickname) {
        for (char letter : nickname.toCharArray()) {
            if (Character.isWhitespace(letter) || Character.isSpaceChar(letter)) { 
                return true;
            }
        }
        return false;
    }
    
    /** 규칙위반 시 해당하는 경고문 리턴*/
    public String findWarningMessage(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return EMPTY_WARNING;
        }
        if (containsWhitespace(nickname)) {
            return SPACE_WARNING;
        }
        if (!ALLOWED_CHARACTERS.matcher(nickname).matches()) {
            return SYMBOL_WARNING;
        }
        if (nickname.length() > MAX_NICKNAME_LENGTH) {
            return LENGTH_WARNING;
        }
        return null;
    }

}