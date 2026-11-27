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
import client.result.GameOverView;
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

// 게임 진행 중 사용자 입력, 서버 메시지 수신, 모델 상태 반영 및 화면 전환을 조율하는 컨트롤러
public class GameController implements ActionListener, GameModelListener {

    private static final int LOOP_INTERVAL_MS = 50; // 약 20 FPS
    private static final int DISCONNECT_REDIRECT_MS = 3000;

    private GameClientModel gameModel;
    private GameView gameView;
    private GameOverView gameOverView;
    private ServerConnection serverConnection;

    private Timer gameLoopTimer;
    private Timer disconnectTimer;

    private int localPlayerId = -1;
    private boolean isGameRunning = false;
    private boolean isHandlingDisconnect = false;

    // 동일한 아이템 효과(Shadow 등)의 중복 적용을 방지하기 위한 마지막 처리 ID
    private String lastProcessedItemId = null;

    private Runnable onReturnToLobby;
    private Runnable onReturnToLogin;

    // 모델, 뷰, 서버 연결 객체를 주입받고 각 리스너를 바인딩
    public GameController(GameClientModel model, GameView view, ServerConnection connection) {
        this.gameModel = model;
        this.gameView = view;
        this.serverConnection = connection;

        if (this.gameModel != null) {
            if (this.gameView != null) {
                this.gameModel.addListener(this.gameView);
            }
            this.gameModel.addListener(this);
        }

        if (this.gameView != null) {
            this.gameView.setWordSubmitListener(this);
        }

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

    // 사용자가 입력한 단어를 검증한 뒤 서버로 제출
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

    // 서버에서 전달된 게임 상태를 모델에 반영하고, 획득된 아이템 효과를 처리
    public void onGameStateReceived(GameStateUpdateMessage update) {
        if (update == null || gameModel == null) {
            return;
        }

        gameModel.applyServerUpdate(update);

        LastClaimedItem claimed = update.getLastClaimedItem();

        if (claimed != null) {
            handleLastClaimedItem(
                String.valueOf(claimed.getItemId()),
                claimed.getType(),
                claimed.getClaimedBy()
            );
        }
    }

    // 최근 획득된 아이템 효과를 적용 (동일한 itemId는 중복 처리하지 않음)
    public void handleLastClaimedItem(String itemId, ItemType itemType, int claimedBy) {
        if (!isGameRunning || itemId == null || itemId.isBlank()
                || itemId.equals(lastProcessedItemId)) {
            return;
        }

        lastProcessedItemId = itemId;

        if (itemType == ItemType.SHADOW) {
            PlayerStateData p1 = gameModel.getPlayer1state();
            PlayerStateData p2 = gameModel.getPlayer2state();

            if (p1 == null || p2 == null) {
                return;
            }

            // 아이템을 획득한 플레이어의 상대방 화면을 가림
            int victimId = (claimedBy == p1.getplayerId())
                    ? p2.getplayerId()
                    : p1.getplayerId();

            onShadowEffectReceived(victimId);
        }
    }

    // 게임 종료 시 로컬 루프를 정지하고 결과 화면(GameOverView)을 표시
    public void onGameFinished(GameResult result) {
        cleanupRoundState(false, false);

        if (gameOverView != null && gameModel != null && gameView != null) {
            int myId = (gameModel.getLocalPlayerId() != -1)
                    ? gameModel.getLocalPlayerId()
                    : this.localPlayerId;

            int player1Id = gameView.getPlayer1Id();
            String p1Nick = gameView.getPlayer1Nickname();
            String p2Nick = gameView.getPlayer2Nickname();

            gameOverView.displayResult(result, myId, player1Id, p1Nick, p2Nick);
            gameOverView.setVisible(true);
        }
    }

    // 라운드 상태를 초기화하고 대기실 화면으로 복귀
    public void onClickRestart() {
        cleanupRoundState(true, true);

        if (gameView != null) {
            gameView.clearConsoleLog();
        }

        if (onReturnToLobby != null) {
            onReturnToLobby.run();
        }
    }

    // 라운드 상태를 초기화하고 서버 연결을 종료한 뒤 로그인 화면으로 복귀
    public void onClickMainMenu() {
        cleanupRoundState(true, true);

        if (serverConnection != null) {
            serverConnection.disconnect();
        }

        if (onReturnToLogin != null) {
            onReturnToLogin.run();
        }
    }

    // 클라이언트 화면 갱신용 렌더링 루프 시작
    public void startClientGameLoop() {
        stopClientGameLoop();
        isGameRunning = true;
        lastProcessedItemId = null;

        if (gameView != null) {
            gameView.clearInputField();
        }

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

    // 게임 중도 퇴장 시 루프를 정지하고 서버에 종료 시그널 전송
    public void onClickQuit() {
        stopClientGameLoop();

        if (serverConnection != null) {
            serverConnection.send(SignalMessage.gameQuit());
        }
    }

    // 단어 입력창 제출 및 결과창 버튼 액션 처리
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

    // 모델 상태 변경 시 패배(HP 0 이하) 여부를 감지해 로컬 루프를 선제 정지
    @Override
    public void onGameStateChanged(GameClientModel model) {
        if (model == null) {
            return;
        }

        PlayerStateData p1 = model.getPlayer1state();
        PlayerStateData p2 = model.getPlayer2state();

        boolean anyDefeated =
                (p1 != null && p1.getCurrentHp() <= 0)
                || (p2 != null && p2.getCurrentHp() <= 0);

        if (anyDefeated && isGameRunning) {
            stopClientGameLoop();
        }
    }

    // 게임 중 서버 연결이 끊겼을 때 안내 오버레이를 3초간 표시한 뒤 로그인 화면으로 복귀
    public void onServerDisconnected() {
        SwingUtilities.invokeLater(() -> {
            if (isHandlingDisconnect) {
                return;
            }

            isHandlingDisconnect = true;

            cleanupRoundState(true, true);

            if (gameView != null) {
                gameView.showDisconnectedOverlay(true);
            }

            if (disconnectTimer == null) {
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
            }

            disconnectTimer.restart();
        });
    }

    // 서버로부터 수신한 JSON 메시지를 디코딩해 타입별로 분기 처리
    private void handleServerMessage(String json) {
        Message message = JsonUtil.decodeOrNull(json, "GameController");

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
            default -> {
                // 게임과 무관한 메시지는 무시
            }
        }
    }

    // 상대방 이탈 시 알림을 띄우고 대기실로 복귀
    public void onOpponentLeft() {
        cleanupRoundState(true, true);

        JOptionPane.showMessageDialog(
            gameView,
            "상대방이 게임을 나갔습니다. 대기실로 돌아갑니다."
        );

        if (onReturnToLobby != null) {
            onReturnToLobby.run();
        }
    }

    // 라운드 종료·중단 시 공통으로 수행하는 루프 정지 및 UI 상태 정리
    private void cleanupRoundState(boolean disposeGameOver, boolean clearInput) {
        stopClientGameLoop();
        lastProcessedItemId = null;

        if (disposeGameOver && gameOverView != null) {
            gameOverView.dispose();
        }

        if (gameView != null) {
            gameView.clearShadowEffects();

            if (clearInput) {
                gameView.clearInputField();
            }
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