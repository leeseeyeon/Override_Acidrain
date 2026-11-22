package client.game;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

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

        // 중앙: 승패 전략 화면이 표시될 영역
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
        this.gameResult = result;
        if (screenStrategy == null || result == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            resultContainerPanel.removeAll();
            // 예: ResultScreen이 JPanel을 반환하거나, container에 컴포넌트를 채우는 방식
            // resultContainerPanel.add(screenStrategy.createResultPanel(result), BorderLayout.CENTER);
            // 또는 screenStrategy.render(resultContainerPanel, result);

            resultContainerPanel.revalidate();
            resultContainerPanel.repaint();
        });
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