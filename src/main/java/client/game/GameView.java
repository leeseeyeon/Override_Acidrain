package client.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class GameView extends JPanel implements GameModelListener {
    private PlayerGameBoardPanel player1BoardPanel;
    private PlayerGameBoardPanel player2BoardPanel;
    private AttackItemPromptPanel centerItemPromptPanel;
    private GameConsoleLogPanel consoleLogPanel;

    private int localPlayerId = 1; // 기본값: Player 1 기준
    private ActionListener wordSubmitListener;

    /** @wbp.parser.constructor */
    public GameView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(226, 232, 240));
        setPreferredSize(new Dimension(920, 560));

        // 중앙 영역: Player 1 보드(좌) - 중앙 아이템 프롬프트(중) - Player 2 보드(우)
        JPanel boardAreaPanel = new JPanel(new BorderLayout(12, 0));
        boardAreaPanel.setOpaque(false);

        player1BoardPanel = new PlayerGameBoardPanel();
        centerItemPromptPanel = new AttackItemPromptPanel();
        player2BoardPanel = new PlayerGameBoardPanel();

        boardAreaPanel.add(player1BoardPanel, BorderLayout.WEST);
        boardAreaPanel.add(centerItemPromptPanel, BorderLayout.CENTER);
        boardAreaPanel.add(player2BoardPanel, BorderLayout.EAST);

        add(boardAreaPanel, BorderLayout.CENTER);

        // 하단 영역: 게임 콘솔 로그
        consoleLogPanel = new GameConsoleLogPanel();
        add(consoleLogPanel, BorderLayout.SOUTH);

        applyLocalPlayerInputState();
    }

    @Override
    public void onGameStateChanged(GameClientModel model) {
        if (model == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            if (consoleLogPanel != null) {
                consoleLogPanel.updateLogs(model.getLog());
            }
        });
    }

    @Override
    public void onPlayer1StateUpdated(GameClientModel model) {
        if (model == null || player1BoardPanel == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            player1BoardPanel.updatePlayerStatus(model.getPlayer1state());
        });
    }

    @Override
    public void onPlayer2StateUpdated(GameClientModel model) {
        if (model == null || player2BoardPanel == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            player2BoardPanel.updatePlayerStatus(model.getPlayer2state());
        });
    }

    @Override
    public void onWordsUpdated(GameClientModel model) {
        if (model == null) {
            return;
        }

        List<ClientFallingWord> allWords = model.getActiveFallingWords();
        List<ClientFallingWord> normalWords = new ArrayList<>();
        AttackItemWord centerItemWord = null;

        if (allWords != null) {
            for (ClientFallingWord word : allWords) {
                if (word == null) {
                    continue;
                }
                if (word instanceof AttackItemWord) {
                    centerItemWord = (AttackItemWord) word;
                } else {
                    normalWords.add(word);
                }
            }
        }

        final AttackItemWord targetCenterItem = centerItemWord;

        SwingUtilities.invokeLater(() -> {
            if (player1BoardPanel != null) {
                player1BoardPanel.updateFallingWords(normalWords);
            }
            if (player2BoardPanel != null) {
                player2BoardPanel.updateFallingWords(normalWords);
            }
            if (centerItemPromptPanel != null) {
                if (targetCenterItem != null) {
                    centerItemPromptPanel.updateFallingItem(targetCenterItem);
                } else {
                    centerItemPromptPanel.clearFallingItem();
                }
            }
        });
    }

    public void setWordSubmitListener(ActionListener listener) {
        this.wordSubmitListener = listener;
        PlayerGameBoardPanel localBoard = getLocalBoardPanel();
        if (localBoard != null && listener != null) {
            localBoard.addWordSubmitListener(listener);
        }
    }

    public void setLocalPlayerId(int playerId) {
        this.localPlayerId = (playerId == 2) ? 2 : 1;
        applyLocalPlayerInputState();

        if (wordSubmitListener != null) {
            setWordSubmitListener(wordSubmitListener);
        }
    }

    public String getEnteredWord() {
        PlayerGameBoardPanel localBoard = getLocalBoardPanel();
        return (localBoard != null) ? localBoard.getEnteredWord() : "";
    }

    public void clearInputField() {
        PlayerGameBoardPanel localBoard = getLocalBoardPanel();
        if (localBoard != null) {
            localBoard.clearInputField();
        }
    }

    public PlayerGameBoardPanel getPlayer1BoardPanel() {
        return player1BoardPanel;
    }

    public PlayerGameBoardPanel getPlayer2BoardPanel() {
        return player2BoardPanel;
    }

    public AttackItemPromptPanel getCenterItemPromptPanel() {
        return centerItemPromptPanel;
    }

    public GameConsoleLogPanel getConsoleLogPanel() {
        return consoleLogPanel;
    }

    private PlayerGameBoardPanel getLocalBoardPanel() {
        return (localPlayerId == 2) ? player2BoardPanel : player1BoardPanel;
    }

    private void applyLocalPlayerInputState() {
        if (player1BoardPanel != null) {
            player1BoardPanel.setInputEditable(localPlayerId == 1);
        }
        if (player2BoardPanel != null) {
            player2BoardPanel.setInputEditable(localPlayerId == 2);
        }
    }
}