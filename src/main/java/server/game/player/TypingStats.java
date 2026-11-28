package server.game.player;

public class TypingStats {
	private int totalTypedChars;
	private double totalTypingTime;
	private int totalTypedWords;//전체 쓴 단어
	private int correctTypedChars;//맞게 친 글자수
	private int correctTypedWords;//맞게 친 단어 수
	
	//생성자
	public TypingStats() {
		this.totalTypedChars = 0;
		this.correctTypedChars = 0;
		this.totalTypingTime =  0;
		this.totalTypedWords = 0;
		this.correctTypedWords=0;
	}
	
	
	//기록 등록_누적됨
	public void recordInput(String word, double typingTime,boolean isCorrect ) {
		totalTypedChars += word.length();
		totalTypingTime += typingTime;
		totalTypedWords ++;
		if(isCorrect) {
			correctTypedChars += word.length();
			correctTypedWords ++;
		}
		
	}
	
	//cpm계산식
	public double calculateCPM() {
		if (totalTypingTime ==0) {
			return 0;
		}
		
		return correctTypedChars/totalTypingTime *60;
		//CPM : 맞게 입력한 글자수 / 전체 입력 시간(초) *60
		//CPM : 1분동안 입력한 글자수 
	}
	
	//정확도 계산식
	public double calculateAccuracy() {
		//Accuracy : 정답 단어 수 / 전체 입력 단어 수 *100
		//단어 기준 정확도
		if(totalTypedWords == 0) {
			return 0;
		}
		
		return (double)correctTypedWords/totalTypedWords *100;
	}
}
