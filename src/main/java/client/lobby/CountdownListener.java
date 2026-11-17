package client.lobby;

/**
 * GameStartCountdown이 진행 상황을 알리는 약속
 */
public interface CountdownListener {
    void onCountdownTicked(int remainingSeconds);

    void onCountdownFinished();
}