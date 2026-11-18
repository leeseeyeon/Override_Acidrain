package client.lobby;

import javax.swing.Timer;   // java.util.Timer와 이름이 같아서 직접 지정

/*
게임 시작 전 3, 2, 1 카운트다운
GAME_START를 받은 각 클라이언트가 스스로 셈 (서버 수정 없음)
 */
public class GameStartCountdown {
    private static final int COUNTDOWN_SECONDS = 3;
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