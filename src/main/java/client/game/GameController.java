package client.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GameController implements ActionListener, GameModelListener {
    private static final int LOOP_INTERVAL_MS = 50; // 약 20 FPS

    private GameClientModel gameModel;
    private GameView gameView;
    private GameOverView gameOverView;
    private ServerConnection serverConnection;

    private Timer gameLoopTimer;
    private int localPlayerId = 1;
    private boolean isGameRunning = false;

    public GameController(GameClientModel model, GameView view, ServerConnection connection) {
        this.gameModel = model;
        this.gameView = view;
        this.serverConnection = connection;

        // 모델에 View와 Controller를 리스너로 등록
        if (this.gameModel != null) {
            if (this.gameView != null) {
                this.gameModel.addListener(this.gameView);
            }
            this.gameModel.addListener(this);
        }

        // View의 단어 입력(엔터) 이벤트를 Controller로 연결
        if (this.gameView != null) {
            this.gameView.setWordSubmitListener(this);
        }
    }

    public void setLocalPlayerId(int playerId) {
        this.localPlayerId = (playerId == 2) ? 2 : 1;
        if (this.gameView != null) {
            this.gameView.setLocalPlayerId(this.localPlayerId);
        }
    }

    public void setGameOverView(GameOverView gameOverView) {
        this.gameOverView = gameOverView;
        bindGameOverViewListeners();
    }

    public void onWordSubmitted(String word) {
        if (!isGameRunning || word == null) {
            return;
        }

        String trimmedWord = word.trim();
        if (trimmedWord.isEmpty()) {
            return;
        }

        if (serverConnection != null) {
            serverConnection.sendWordInput(trimmedWord);
        }
    }

    public void onGameStateReceived(GameStateUpdate update) {
        if (update == null || gameModel == null) {
            return;
        }

        // 모델 상태 반영 -> 등록된 GameModelListener(GameView, GameController)로 자동 통지
        gameModel.applyServerUpdate(update);
    }

    public void onGameFinished(GameResult result) {
        stopClientGameLoop();

        SwingUtilities.invokeLater(() -> {
            if (gameOverView == null) {
                gameOverView = new GameOverView();
                bindGameOverViewListeners();
            }

            boolean isWinner = (result != null && result.getWinnerPlayerId() == localPlayerId);
            gameOverView.setScreenStrategy(isWinner ? new WinnerScreen() : new LoserScreen());
            gameOverView.displayResult(result);
            gameOverView.setVisible(true);
        });
    }

    public void onClickRestart() {
        stopClientGameLoop();

        if (gameOverView != null) {
            gameOverView.dispose();
        }
        if (gameView != null) {
            gameView.clearInputField();
            if (gameView.getConsoleLogPanel() != null) {
                gameView.getConsoleLogPanel().clearConsoleLog();
            }
        }
        if (serverConnection != null) {
            serverConnection.sendRestartRequest();
        }
    }

    public void onClickMainMenu() {
        stopClientGameLoop();

        if (gameOverView != null) {
            gameOverView.dispose();
        }
        if (serverConnection != null) {
            serverConnection.sendMainMenuRequest();
        }
        // TODO: 메인 프레임(CardLayout 등)을 메인(닉네임 입력) 화면으로 전환
    }

    public void startClientGameLoop() {
        stopClientGameLoop();
        isGameRunning = true;

        gameLoopTimer = new Timer(LOOP_INTERVAL_MS, e -> {
            if (!isGameRunning || gameModel == null) {
                return;
            }

            List<ClientFallingWord> activeWords = gameModel.getActiveFallingWords();
            if (activeWords != null) {
                // 순회 중 서버 업데이트로 리스트가 변경되는 예외 방지를 위해 복사본 순회
                for (ClientFallingWord word : new ArrayList<>(activeWords)) {
                    if (word != null) {
                        word.moveWord();
                    }
                }
            }

            if (gameView != null) {
                gameView.onWordsUpdated(gameModel);
            }
        });
        gameLoopTimer.start();
    }

    public void stopClientGameLoop() {
        isGameRunning = false;
        if (gameLoopTimer != null && gameLoopTimer.isRunning()) {
            gameLoopTimer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e == null) {
            return;
        }

        // 단어 입력창(JTextField)에서 엔터 입력 시
        if (e.getSource() instanceof JTextField) {
            if (gameView != null) {
                String enteredWord = gameView.getEnteredWord();
                onWordSubmitted(enteredWord);
            }
            return;
        }

        // 버튼 이벤트가 actionPerformed로 직접 들어온 경우 처리
        String command = e.getActionCommand();
        if ("재시작".equals(command)) {
            onClickRestart();
        } else if ("메인 화면".equals(command)) {
            onClickMainMenu();
        }
    }

    @Override
    public void onGameStateChanged(GameClientModel model) {
        if (model == null) {
            return;
        }

        // 둘 중 한 명의 HP가 0 이하가 되면 로컬 낙하 루프를 먼저 정지
        PlayerStateData p1 = model.getPlayer1state();
        PlayerStateData p2 = model.getPlayer2state();
        boolean anyDefeated =
                (p1 != null && p1.getCurrentHp() <= 0) ||
                (p2 != null && p2.getCurrentHp() <= 0);

        if (anyDefeated && isGameRunning) {
            stopClientGameLoop();
        }
    }

    private void bindGameOverViewListeners() {
        if (gameOverView == null) {
            return;
        }
        gameOverView.setRestartButtonListener(e -> onClickRestart());
        gameOverView.setMainMenuButtonListener(e -> onClickMainMenu());
    }
}