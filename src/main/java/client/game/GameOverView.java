package client.game;

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

public class GameOverView extends JFrame {
    private ResultScreen screenStrategy;
    private GameResult gameResult;

    private JPanel contentPane;
    private JPanel resultContainerPanel; // 전략별(승리/패배) UI가 들어갈 영역
    private JButton restartButton;
    private JButton mainMenuButton;

    public GameOverView() {
        setTitle("게임 결과");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 450);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        // 중앙: 승패 화면이 표시될 영역
        resultContainerPanel = new JPanel(new BorderLayout());
        contentPane.add(resultContainerPanel, BorderLayout.CENTER);

        // 하단: 재시작 / 메인 메뉴 버튼 영역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        restartButton = new JButton("재시작");
        buttonPanel.add(restartButton);

        mainMenuButton = new JButton("메인 화면");
        buttonPanel.add(mainMenuButton);
    }

    public void setScreenStrategy(ResultScreen strategy) {
        this.screenStrategy = strategy;
    }

    public void displayResult(GameResult result) {
        displayResult(result, 1, 1, "Player 1", "Player 2");
    }

    public void displayResult(GameResult result, int localPlayerId, int player1Id,
                              String player1Nickname, String player2Nickname) {
        this.gameResult = result;
        if (result == null) {
            return;
        }

        int winnerId = result.getWinnerId();
        boolean isLocalWin = (winnerId == localPlayerId);
        boolean isPlayer1Winner = (winnerId == player1Id);

        String p1Name = (player1Nickname != null && !player1Nickname.isBlank()) ? player1Nickname : "Player 1";
        String p2Name = (player2Nickname != null && !player2Nickname.isBlank()) ? player2Nickname : "Player 2";

        String winnerName = isPlayer1Winner ? p1Name : p2Name;
        double winnerCpm = isPlayer1Winner ? result.getPlayer1CPM() : result.getPlayer2CPM();
        double winnerAcc = isPlayer1Winner ? result.getPlayer1Accuracy() : result.getPlayer2Accuracy();

        String loserName = isPlayer1Winner ? p2Name : p1Name;
        double loserCpm = isPlayer1Winner ? result.getPlayer2CPM() : result.getPlayer1CPM();
        double loserAcc = isPlayer1Winner ? result.getPlayer2Accuracy() : result.getPlayer1Accuracy();

        SwingUtilities.invokeLater(() -> {
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

    private JPanel createDefaultResultPanel(boolean isLocalWin,
                                            String winnerName, double winnerCpm, double winnerAcc,
                                            String loserName, double loserCpm, double loserAcc) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 208, 220), 2),
                new EmptyBorder(24, 24, 24, 24)
        ));

        // 상단 승패 타이틀
        JLabel titleLabel = new JLabel(isLocalWin ? "VICTORY!" : "DEFEAT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 34));
        titleLabel.setForeground(isLocalWin ? new Color(46, 204, 113) : new Color(231, 76, 60));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 중앙 승자 / 패자 통계 카드
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 14));
        statsPanel.setOpaque(false);

        statsPanel.add(createPlayerStatCard("승자", winnerName, winnerCpm, winnerAcc, new Color(46, 204, 113)));
        statsPanel.add(createPlayerStatCard("패자", loserName, loserCpm, loserAcc, new Color(148, 163, 184)));

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPlayerStatCard(String role, String nickname, double cpm, double accuracy, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel nameLabel = new JLabel("[" + role + "] " + nickname);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nameLabel.setForeground(new Color(30, 41, 59));

        JLabel statLabel = new JLabel(String.format("타수(CPM): %.1f   |   정확도: %.1f%%", cpm, accuracy));
        statLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        statLabel.setForeground(new Color(71, 85, 105));

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(statLabel, BorderLayout.CENTER);
        return card;
    }

    public void setRestartButtonListener(ActionListener listener) {
        for (ActionListener old : restartButton.getActionListeners()) {
            restartButton.removeActionListener(old);
        }
        restartButton.addActionListener(listener);
    }

    public void setMainMenuButtonListener(ActionListener listener) {
        for (ActionListener old : mainMenuButton.getActionListeners()) {
            mainMenuButton.removeActionListener(old);
        }
        mainMenuButton.addActionListener(listener);
    }
}