package client.lobby.countdown;

public interface CountdownListener {
    void onCountdownTicked(int remainingSeconds);

    void onCountdownFinished();
}