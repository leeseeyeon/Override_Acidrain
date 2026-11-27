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

import common.dto.AttackItemWord;
import common.dto.ClientFallingWord;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GameView extends JPanel implements GameModelListener {
    private PlayerGameBoardPanel player1BoardPanel;
    private PlayerGameBoardPanel player2BoardPanel;
    private ItemWordPanel centerItemPromptPanel;
    private GameConsoleLogPanel consoleLogPanel;

    private int localPlayerId = -1;
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
        centerItemPromptPanel = new ItemWordPanel();
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
            applyLocalPlayerInputState();
        });
    }

    @Override
    public void onPlayer2StateUpdated(GameClientModel model) {
        if (model == null || player2BoardPanel == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            player2BoardPanel.updatePlayerStatus(model.getPlayer2state());
            applyLocalPlayerInputState();
        });
    }

    @Override
    public void onWordsUpdated(GameClientModel model) {
        if (model == null) {
            return;
        }

        List<ClientFallingWord> player1Words = (model.getPlayer1FallingWords() != null)
                ? new ArrayList<>(model.getPlayer1FallingWords())
                : new ArrayList<>();
        List<ClientFallingWord> player2Words = (model.getPlayer2FallingWords() != null)
                ? new ArrayList<>(model.getPlayer2FallingWords())
                : new ArrayList<>();
        AttackItemWord centerItemWord = model.getCenterItemWord();

        SwingUtilities.invokeLater(() -> {
            if (player1BoardPanel != null) {
                player1BoardPanel.updateFallingWords(player1Words);
            }
            if (player2BoardPanel != null) {
                player2BoardPanel.updateFallingWords(player2Words);
            }
            if (centerItemPromptPanel != null) {
                if (centerItemWord != null) {
                    centerItemPromptPanel.updateFallingItem(centerItemWord);
                } else {
                    centerItemPromptPanel.clearFallingItem();
                }
            }
        });
    }

    public void setWordSubmitListener(ActionListener listener) {
        this.wordSubmitListener = listener;
        if (listener == null) {
            return;
        }
        if (player1BoardPanel != null) {
            player1BoardPanel.addWordSubmitListener(listener);
        }
        if (player2BoardPanel != null) {
            player2BoardPanel.addWordSubmitListener(listener);
        }
    }

    public void setLocalPlayerId(int playerId) {
        this.localPlayerId = playerId;
        applyLocalPlayerInputState();
    }

    public String getEnteredWord() {
        PlayerGameBoardPanel localBoard = getLocalBoardPanel();
        return (localBoard != null) ? localBoard.getEnteredWord() : "";
    }

    public double consumeTypingTime() {
        PlayerGameBoardPanel localBoard = getLocalBoardPanel();
        return (localBoard != null) ? localBoard.consumeTypingTime() : 0.0;
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

    public ItemWordPanel getCenterItemPromptPanel() {
        return centerItemPromptPanel;
    }

    public GameConsoleLogPanel getConsoleLogPanel() {
        return consoleLogPanel;
    }

    private PlayerGameBoardPanel getLocalBoardPanel() {
        if (player1BoardPanel != null && player1BoardPanel.getplayerId() == localPlayerId) {
            return player1BoardPanel;
        }
        if (player2BoardPanel != null && player2BoardPanel.getplayerId() == localPlayerId) {
            return player2BoardPanel;
        }
        return (localPlayerId == 2) ? player2BoardPanel : player1BoardPanel;
    }

    private void applyLocalPlayerInputState() {
        boolean isP1Local = (player1BoardPanel != null && player1BoardPanel.getplayerId() != -1)
                ? (player1BoardPanel.getplayerId() == localPlayerId)
                : (localPlayerId == 1);
        boolean isP2Local = (player2BoardPanel != null && player2BoardPanel.getplayerId() != -1)
                ? (player2BoardPanel.getplayerId() == localPlayerId)
                : (localPlayerId == 2);

        if (player1BoardPanel != null) {
            player1BoardPanel.setInputEditable(isP1Local);
        }
        if (player2BoardPanel != null) {
            player2BoardPanel.setInputEditable(isP2Local);
        }
    }

    public void clearConsoleLog() {
        if (consoleLogPanel == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            consoleLogPanel.clearConsoleLog();
        });
    }

    public void clearShadowEffects() {
        SwingUtilities.invokeLater(() -> {
            if (player1BoardPanel != null) {
                player1BoardPanel.clearShadowEffect();
            }
            if (player2BoardPanel != null) {
                player2BoardPanel.clearShadowEffect();
            }
        });
    }

    public void triggerShadowEffect(int targetPlayerId) {
        SwingUtilities.invokeLater(() -> {
            if (player1BoardPanel != null && player1BoardPanel.getplayerId() == targetPlayerId) {
                player1BoardPanel.triggerShadowEffect();
            } else if (player2BoardPanel != null && player2BoardPanel.getplayerId() == targetPlayerId) {
                player2BoardPanel.triggerShadowEffect();
            }
        });
    }
    
    private boolean isServerDisconnected = false;

    public void showDisconnectedOverlay(boolean show) {
        SwingUtilities.invokeLater(() -> {
            this.isServerDisconnected = show;
            if (show) {
                if (player1BoardPanel != null) {
                    player1BoardPanel.setInputEditable(false);
                }
                if (player2BoardPanel != null) {
                    player2BoardPanel.setInputEditable(false);
                }
            }
            repaint();
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (isServerDisconnected) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 화면 전체 어둡게 덮기
            g2d.setColor(new Color(15, 23, 42, 200));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // 큰 경고 문구
            String title = "서버 연결 끊김";
            g2d.setFont(new Font("맑은 고딕", Font.BOLD, 44));
            g2d.setColor(new Color(248, 113, 113));
            FontMetrics titleFm = g2d.getFontMetrics();
            int titleX = (getWidth() - titleFm.stringWidth(title)) / 2;
            int titleY = (getHeight() / 2) - 10;
            g2d.drawString(title, titleX, titleY);

            // 하단 안내 문구
            String sub = "잠시 후 로그인 화면으로 이동합니다...";
            g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            g2d.setColor(new Color(226, 232, 240));
            FontMetrics subFm = g2d.getFontMetrics();
            int subX = (getWidth() - subFm.stringWidth(sub)) / 2;
            int subY = titleY + 36;
            g2d.drawString(sub, subX, subY);

            g2d.dispose();
        }
    }

    public int getPlayer1Id() {
        return (player1BoardPanel != null) ? player1BoardPanel.getplayerId() : -1;
    }

    public String getPlayer1Nickname() {
        return player1BoardPanel.getNickname();
    }

    public String getPlayer2Nickname() {
        return player2BoardPanel.getNickname();
    }
}