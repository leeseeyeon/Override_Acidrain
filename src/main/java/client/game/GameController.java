package client.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import client.network.MessageListener;
import client.network.ServerConnection;
import client.ui.*;
import common.dto.GameResult;
import common.dto.LastClaimedItem;
import common.dto.PlayerStateData;
import common.message.JsonUtil;
import common.message.Message;
import common.message.MessageType;
import common.message.SignalMessage;
import common.message.game.*;
import common.type.ItemType;

public class GameController implements ActionListener, GameModelListener {
    private static final int LOOP_INTERVAL_MS = 50; // 약 20 FPS
    private static final int DISCONNECT_REDIRECT_MS = 3000; // 3초

    private GameClientModel gameModel;
    private GameView gameView;
    private GameOverView gameOverView;
    private ServerConnection serverConnection;

    private Timer gameLoopTimer;
    private Timer disconnectTimer;
    private int localPlayerId = -1;
    private boolean isGameRunning = false;
    private boolean isHandlingDisconnect = false;

    // 동일한 itemId에 대한 Shadow 효과 중복 적용 방지용
    private String lastProcessedItemId = null;

    private Runnable onReturnToLobby;
    private Runnable onReturnToLogin;

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
                public void update(String json) {
                    SwingUtilities.invokeLater(() -> handleServerMessage(json));
                }

                @Override
                public void onDisconnected() {
                    if (isGameRunning) {
                        onServerDisconnected();
                    }
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

    public void setOnReturnToLobby(Runnable onReturnToLobby) {
        this.onReturnToLobby = onReturnToLobby;
    }

    public void setOnReturnToLogin(Runnable onReturnToLogin) {
        this.onReturnToLogin = onReturnToLogin;
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

        LastClaimedItem claimed = update.getLastClaimedItem();
        if (claimed != null) {
            handleLastClaimedItem(String.valueOf(claimed.getItemId()), claimed.getType(), claimed.getClaimedBy());
        }
    }

    /**
     * 최근 획득된 아이템 정보를 처리합니다.
     * 동일한 itemId에 대해서는 효과가 중복 적용되지 않습니다.
     */
    public void handleLastClaimedItem(String itemId, ItemType itemType, int claimedBy) {
        if (!isGameRunning || itemId == null || itemId.isBlank() || itemId.equals(lastProcessedItemId)) {
            return;
        }

        lastProcessedItemId = itemId;

        if (itemType == ItemType.SHADOW) {
            // 먹은 사람의 상대방 판을 가림 (두 화면 모두 같은 판이 가려짐)
            PlayerStateData p1 = gameModel.getPlayer1state();
            PlayerStateData p2 = gameModel.getPlayer2state();
            if (p1 == null || p2 == null) {
                return;
            }

            int victimId = (claimedBy == p1.getplayerId()) ? p2.getplayerId() : p1.getplayerId();
            onShadowEffectReceived(victimId);
        }
    }

    public void onGameFinished(GameResult result) {
        stopClientGameLoop();
        lastProcessedItemId = null;

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
        lastProcessedItemId = null;

        if (gameOverView != null) {
            gameOverView.dispose();
        }
        if (gameView != null) {
            gameView.clearInputField();
            gameView.clearConsoleLog();
            gameView.clearShadowEffects();
        }

        // 대기실 화면 전환
        if (onReturnToLobby != null) {
            onReturnToLobby.run();
        }
    }

    public void onClickMainMenu() {
        stopClientGameLoop();
        lastProcessedItemId = null;

        if (gameOverView != null) {
            gameOverView.dispose();
        }

        if (serverConnection != null) {
            serverConnection.disconnect();
        }

        if (onReturnToLogin != null) {
            onReturnToLogin.run();
        }
    }

    public void startClientGameLoop() {
        stopClientGameLoop();
        isGameRunning = true;
        lastProcessedItemId = null;

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

    public void onServerDisconnected() {
        SwingUtilities.invokeLater(() -> {
            if (isHandlingDisconnect) {
                return;
            }
            isHandlingDisconnect = true;

            stopClientGameLoop();
            lastProcessedItemId = null;

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

    private void handleServerMessage(String json) {
        Message message = decodeOrNull(json);
        if (message == null) {
            return;
        }

        switch (message.getType()) {
            case MessageType.GAME_STATE_UPDATE -> {
                GameStateUpdateMessage update = (GameStateUpdateMessage) message;
                onGameStateReceived(update);
            }
            case MessageType.GAME_FINISHED -> {
                GameFinishedMessage finished = (GameFinishedMessage) message;
                onGameFinished(finished.getResult());
            }
            case MessageType.OPPONENT_LEFT -> {
                if (isGameRunning) {
                    onOpponentLeft();
                }
            }
            default -> { /* 게임과 무관한 메시지는 무시 */ }
        }
    }

    public void onOpponentLeft() {
        stopClientGameLoop();
        lastProcessedItemId = null;

        if (gameOverView != null) {
            gameOverView.dispose();
        }
        if (gameView != null) {
            gameView.clearShadowEffects();
        }

        JOptionPane.showMessageDialog(gameView, "상대방이 게임을 나갔습니다. 대기실로 돌아갑니다.");

        if (onReturnToLobby != null) {
            onReturnToLobby.run();
        }
    }

    private Message decodeOrNull(String json) {
        try {
            return JsonUtil.decode(json);
        } catch (RuntimeException e) {
            System.err.println("[GameController] 해석할 수 없는 메시지: " + json);
            return null;
        }
    }

    private void bindGameOverViewListeners() {
        if (gameOverView == null) {
            return;
        }
        gameOverView.setRestartButtonListener(e -> onClickRestart());
        gameOverView.setMainMenuButtonListener(e -> onClickMainMenu());
        gameOverView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClickMainMenu();
            }
        });
    }

    public void onShadowEffectReceived(int targetPlayerId) {
        if (!isGameRunning || gameView == null) {
            return;
        }
        gameView.triggerShadowEffect(targetPlayerId);
    }
}