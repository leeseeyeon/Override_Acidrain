package common.dto;

public class GameResult {

    private int winnerId;
    private double player1CPM;
    private double player2CPM;
    private double player1Accuracy;
    private double player2Accuracy;

    // 생성자
    public GameResult(int winnerId, double player1CPM,
            double player2CPM, double player1Accuracy,
            double player2Accuracy) {

        this.winnerId = winnerId;
        this.player1CPM = player1CPM;
        this.player2CPM = player2CPM;
        this.player1Accuracy = player1Accuracy;
        this.player2Accuracy = player2Accuracy;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public double getPlayer1CPM() {
        return player1CPM;
    }

    public double getPlayer2CPM() {
        return player2CPM;
    }

    public double getPlayer1Accuracy() {
        return player1Accuracy;
    }

    public double getPlayer2Accuracy() {
        return player2Accuracy;
    }
}
