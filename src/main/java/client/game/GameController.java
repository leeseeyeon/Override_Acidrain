package client.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.Timer;

import client.network.MessageListener;
import client.network.ServerConnection;
import common.GameResult;
import common.message.SignalMessage;
import common.message.game.*;
import javax.swing.SwingUtilities;

public class GameController implements ActionListener, GameModelListener {
    private static final int LOOP_INTERVAL_MS = 50; // 약 20 FPS

    private GameClientModel gameModel;
    private GameView gameView;
    private GameOverView gameOverView;
    private ServerConnection serverConnection;

    private Timer gameLoopTimer;
    private int localPlayerId = -1;
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
        
     // 서버 연결 끊김 감지 시 3초 오버레이 후 StartView로 복귀
        if (this.serverConnection != null) {
        	this.serverConnection.addListener(new MessageListener() {
        	    @Override
        	    public void update(String msg) {}

        	    @Override
        	    public void onDisconnected() {
        	        onServerDisconnected();
        	    }
        	});
        }
    }

    public void setLocalPlayerId(int playerId) {
        this.localPlayerId = playerId;
        if (this.gameModel != null) {
            this.gameModel.setLocalPlayerId(playerId);
        }
        if (this.gameView != null) {
            this.gameView.setLocalPlayerId(playerId);
        }
    }

    public void setGameOverView(GameOverView gameOverView) {
        this.gameOverView = gameOverView;
        bindGameOverViewListeners();
    }

    public void onWordSubmitted(String word, double typingTime) {
        if (!isGameRunning || word == null) {
            return;
        }

        String trimmedWord = word.trim();
        if (trimmedWord.isEmpty()) {
            return;
        }

        if (serverConnection != null) {
            WordSubmitMessage message = new WordSubmitMessage(trimmedWord, typingTime);
            serverConnection.send(message);
        }
    }

    public void onGameStateReceived(GameStateUpdateMessage update) {
        if (update == null || gameModel == null) {
            return;
        }

        // 모델 상태 반영 -> 등록된 GameModelListener(GameView, GameController)로 자동 통지
        gameModel.applyServerUpdate(update);
    }

    public void onGameFinished(GameResult result) {
        stopClientGameLoop();

        if (gameView != null) {
            gameView.clearShadowEffects();
        }

        if (gameOverView != null && gameModel != null && gameView != null) {
            int myId = (gameModel.getLocalPlayerId() != -1) ? gameModel.getLocalPlayerId() : this.localPlayerId;
            int player1Id = gameView.getPlayer1Id();
            String p1Nick = gameView.getPlayer1Nickname();
            String p2Nick = gameView.getPlayer2Nickname();

            gameOverView.displayResult(result, myId, player1Id, p1Nick, p2Nick);
            gameOverView.setVisible(true);
        }
    }

    public void onClickRestart() {
        stopClientGameLoop();

        if (gameOverView != null) {
            gameOverView.dispose();
        }
        if (gameView != null) {
            gameView.clearInputField();
            gameView.clearConsoleLog();
            gameView.clearShadowEffects();
        }

        if (serverConnection != null) {
            GameRestartMessage message = new GameRestartMessage();
            serverConnection.send(message);
        }
    }

    public void onClickMainMenu() {
        stopClientGameLoop();

        if (gameOverView != null) {
            gameOverView.dispose();
        }

        if (serverConnection != null) {
            serverConnection.disconnect();
        }
    }

    public void startClientGameLoop() {
        stopClientGameLoop();
        isGameRunning = true;

        if (gameView != null) {
            gameView.clearInputField();
        }

        // 단어 좌표 계산은 서버가 담당하므로 클라이언트는 화면 갱신(렌더링)만 수행
        gameLoopTimer = new Timer(LOOP_INTERVAL_MS, e -> {
            if (!isGameRunning || gameView == null) {
                return;
            }
            gameView.repaint();
        });
        gameLoopTimer.start();
    }

    public void stopClientGameLoop() {
        isGameRunning = false;
        if (gameLoopTimer != null && gameLoopTimer.isRunning()) {
            gameLoopTimer.stop();
        }
    }

    public void onClickQuit() {
        stopClientGameLoop();

        if (serverConnection != null) {
        	serverConnection.send(SignalMessage.gameQuit());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e == null) {
            return;
        }

        if (e.getSource() instanceof JTextField) {
            if (gameView != null) {
                String enteredWord = gameView.getEnteredWord();
                double typingTime = gameView.consumeTypingTime();
                gameView.clearInputField();
                onWordSubmitted(enteredWord, typingTime);
            }
            return;
        }

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

        // 둘 중 한 명의 HP가 0 이하가 되면 로컬 루프를 먼저 정지
        PlayerStateData p1 = model.getPlayer1state();
        PlayerStateData p2 = model.getPlayer2state();
        boolean anyDefeated =
                (p1 != null && p1.getCurrentHp() <= 0) ||
                (p2 != null && p2.getCurrentHp() <= 0);

        if (anyDefeated && isGameRunning) {
            stopClientGameLoop();
        }
    }
    
    private static final int DISCONNECT_REDIRECT_MS = 3000; // 3초

    private Timer disconnectTimer;
    private boolean isHandlingDisconnect = false;
    private Runnable onReturnToLogin;

    public void setOnReturnToLogin(Runnable onReturnToLogin) {
        this.onReturnToLogin = onReturnToLogin;
    }

    public void onServerDisconnected() {
        SwingUtilities.invokeLater(() -> {
            if (isHandlingDisconnect) {
                return;
            }
            isHandlingDisconnect = true;

            stopClientGameLoop();

            if (gameOverView != null) {
                gameOverView.dispose();
            }
            if (gameView != null) {
                gameView.clearShadowEffects();
                gameView.showDisconnectedOverlay(true);
            }

            if (disconnectTimer != null && disconnectTimer.isRunning()) {
                disconnectTimer.stop();
            }

            disconnectTimer = new Timer(DISCONNECT_REDIRECT_MS, e -> {
                ((Timer) e.getSource()).stop();
                isHandlingDisconnect = false;

                if (gameView != null) {
                    gameView.showDisconnectedOverlay(false);
                }
                if (onReturnToLogin != null) {
                    onReturnToLogin.run();
                }
            });
            disconnectTimer.setRepeats(false);
            disconnectTimer.start();
        });
    }

    private void bindGameOverViewListeners() {
        if (gameOverView == null) {
            return;
        }
        gameOverView.setRestartButtonListener(e -> onClickRestart());
        gameOverView.setMainMenuButtonListener(e -> onClickMainMenu());
    }

    public void onShadowEffectReceived(int targetPlayerId) {
        if (!isGameRunning || gameView == null) {
            return;
        }
        gameView.triggerShadowEffect(targetPlayerId);
    }
    
    
}