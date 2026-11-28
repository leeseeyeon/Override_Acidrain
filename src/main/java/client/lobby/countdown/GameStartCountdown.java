package client.lobby.countdown;

import javax.swing.Timer; 

import common.config.GameConfig;

/**
게임 화면으로 넘어가기 전 3, 2, 1 카운트다운
 */
public class GameStartCountdown {
    private static final int COUNTDOWN_SECONDS = GameConfig.START_COUNTDOWN_SECONDS;  
    private static final int TICK_INTERVAL_MILLIS = 1000;

    private int remainingSeconds;
    private Timer timer;
    private CountdownListener listener;

    public void start(CountdownListener listener) {
        cancel();
        this.listener = listener;
        remainingSeconds = COUNTDOWN_SECONDS;
        listener.onCountdownTicked(remainingSeconds);

        timer = new Timer(TICK_INTERVAL_MILLIS, e -> onTimerTicked());
        timer.start();
    }


    public void cancel() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private void onTimerTicked() {
        remainingSeconds--;
        if (remainingSeconds > 0) {
            listener.onCountdownTicked(remainingSeconds);
            return;
        }
        cancel();
        listener.onCountdownFinished();
    }
}