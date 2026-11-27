package client.result;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import common.GameResult;

// 게임 종료 후 승패 결과와 플레이어별 타수·정확도 통계를 표시하는 결과 창
public class GameOverView extends JFrame {
    private static final int WINDOW_X = 100;
    private static final int WINDOW_Y = 100;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 450;

    private static final Color PANEL_BG_COLOR = new Color(248, 250, 252);
    private static final Color PANEL_BORDER_COLOR = new Color(200, 208, 220);
    private static final Color WIN_COLOR = new Color(46, 204, 113);
    private static final Color LOSE_COLOR = new Color(231, 76, 60);
    private static final Color LOSER_CARD_BORDER_COLOR = new Color(148, 163, 184);
    private static final Color NAME_TEXT_COLOR = new Color(30, 41, 59);
    private static final Color STAT_TEXT_COLOR = new Color(71, 85, 105);

    private static final Font TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 34);
    private static final Font NAME_FONT = new Font("맑은 고딕", Font.BOLD, 16);
    private static final Font STAT_FONT = new Font("맑은 고딕", Font.PLAIN, 14);

    private ResultScreen screenStrategy;
    private GameResult gameResult;

    private JPanel resultContainerPanel;
    private JButton restartButton;
    private JButton mainMenuButton;

    public GameOverView() {
        setTitle("게임 결과");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(WINDOW_X, WINDOW_Y, WINDOW_WIDTH, WINDOW_HEIGHT);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        // 중앙: 승패 결과 및 통계 카드 영역
        resultContainerPanel = new JPanel(new BorderLayout());
        contentPane.add(resultContainerPanel, BorderLayout.CENTER);

        // 하단: 재시작 / 메인 화면 버튼 영역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        restartButton = new JButton("재시작");
        mainMenuButton = new JButton("메인 화면");
        buttonPanel.add(restartButton);
        buttonPanel.add(mainMenuButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setScreenStrategy(ResultScreen strategy) {
        this.screenStrategy = strategy;
    }

    // 기본 플레이어 정보로 결과 화면 표시
    public void displayResult(GameResult result) {
        displayResult(result, 1, 1, "Player 1", "Player 2");
    }

    // 로컬 플레이어 기준 승패와 양측 통계를 계산해 결과 화면 갱신
    public void displayResult(GameResult result, int localPlayerId, int player1Id,
                              String player1Nickname, String player2Nickname) {
        this.gameResult = result;
        if (result == null) {
            return;
        }

        int winnerId = result.getWinnerId();
        boolean isLocalWin = (winnerId == localPlayerId);
        boolean isPlayer1Winner = (winnerId == player1Id);

        String p1Name = resolveNickname(player1Nickname, "Player 1");
        String p2Name = resolveNickname(player2Nickname, "Player 2");

        String winnerName = isPlayer1Winner ? p1Name : p2Name;
        double winnerCpm = isPlayer1Winner ? result.getPlayer1CPM() : result.getPlayer2CPM();
        double winnerAcc = isPlayer1Winner ? result.getPlayer1Accuracy() : result.getPlayer2Accuracy();

        String loserName = isPlayer1Winner ? p2Name : p1Name;
        double loserCpm = isPlayer1Winner ? result.getPlayer2CPM() : result.getPlayer1CPM();
        double loserAcc = isPlayer1Winner ? result.getPlayer2Accuracy() : result.getPlayer1Accuracy();

        runOnEdt(() -> {
            resultContainerPanel.removeAll();
            resultContainerPanel.add(
                    createDefaultResultPanel(isLocalWin, winnerName, winnerCpm, winnerAcc,
                            loserName, loserCpm, loserAcc),
                    BorderLayout.CENTER
            );
            resultContainerPanel.revalidate();
            resultContainerPanel.repaint();
        });
    }

    // 승패 타이틀과 승자·패자 통계 카드를 포함한 결과 패널 생성
    private JPanel createDefaultResultPanel(boolean isLocalWin,
                                            String winnerName, double winnerCpm, double winnerAcc,
                                            String loserName, double loserCpm, double loserAcc) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(PANEL_BG_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PANEL_BORDER_COLOR, 2),
                new EmptyBorder(24, 24, 24, 24)
        ));

        JLabel titleLabel = new JLabel(isLocalWin ? "VICTORY!" : "DEFEAT", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(isLocalWin ? WIN_COLOR : LOSE_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 14));
        statsPanel.setOpaque(false);
        statsPanel.add(createPlayerStatCard("승자", winnerName, winnerCpm, winnerAcc, WIN_COLOR));
        statsPanel.add(createPlayerStatCard("패자", loserName, loserCpm, loserAcc, LOSER_CARD_BORDER_COLOR));

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    // 개별 플레이어의 역할, 닉네임, 타수(CPM), 정확도 카드 UI 생성
    private JPanel createPlayerStatCard(String role, String nickname, double cpm, double accuracy, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel nameLabel = new JLabel("[" + role + "] " + nickname);
        nameLabel.setFont(NAME_FONT);
        nameLabel.setForeground(NAME_TEXT_COLOR);

        JLabel statLabel = new JLabel(String.format("타수(CPM): %.1f   |   정확도: %.1f%%", cpm, accuracy));
        statLabel.setFont(STAT_FONT);
        statLabel.setForeground(STAT_TEXT_COLOR);

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(statLabel, BorderLayout.CENTER);
        return card;
    }

    public void setRestartButtonListener(ActionListener listener) {
        replaceActionListener(restartButton, listener);
    }

    public void setMainMenuButtonListener(ActionListener listener) {
        replaceActionListener(mainMenuButton, listener);
    }

    // 기존 버튼 리스너를 모두 제거하고 단일 리스너로 교체
    private void replaceActionListener(JButton button, ActionListener listener) {
        for (ActionListener old : button.getActionListeners()) {
            button.removeActionListener(old);
        }
        button.addActionListener(listener);
    }

    private String resolveNickname(String nickname, String fallback) {
        return (nickname != null && !nickname.isBlank()) ? nickname : fallback;
    }

    // Swing UI 갱신을 EDT(Event Dispatch Thread)에서 안전하게 실행
    private void runOnEdt(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
}