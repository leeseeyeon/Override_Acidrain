package client.lobby;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

//채팅 영역 (View 부품) - 입력칸에서 Enter를 누르면 전송

public class ChatPanel extends JPanel {
    private JScrollPane chatHistoryScrollPane;
    private JTextArea chatHistoryArea;
    private JTextField chatInputField;

    public ChatPanel() {
        setBackground(new Color(235, 235, 235));
        setPreferredSize(new Dimension(920, 170));
        setLayout(null);

        chatHistoryScrollPane = new JScrollPane();
        chatHistoryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatHistoryScrollPane.setBounds(16, 10, 888, 112);
        add(chatHistoryScrollPane);

        chatHistoryArea = new JTextArea();
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setLineWrap(true);
        chatHistoryArea.setBackground(new Color(235, 235, 235));
        chatHistoryArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatHistoryScrollPane.setViewportView(chatHistoryArea);

        chatInputField = new JTextField();
        chatInputField.setToolTipText("메시지를 입력하고 Enter를 누르세요");
        chatInputField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatInputField.setBounds(16, 114, 888, 44);
        add(chatInputField);

        // Enter 후 입력칸 비우기 (입력한 내용은 ActionEvent에 이미 담겨 있어서 먼저 비워도 ok.)
        chatInputField.addActionListener(e -> chatInputField.setText(""));
    }


// ===== LobbyView가 쓰는 메소드 (유지) =====

    //채팅 한 줄을 맨 아래에 추가
    public void appendChatMessage(ChatMessage message) {
        if (message.isSystemMessage()) {
            chatHistoryArea.append("[알림] " + message.getContent() + "\n");
        } else {
            chatHistoryArea.append(message.getSenderNickname() + " : " + message.getContent() + "\n");
        }
        chatHistoryArea.setCaretPosition(chatHistoryArea.getDocument().getLength());  // 맨 아래로 스크롤
    }

    //채팅창 비우기 (방에 새로 들어왔을 때)
    public void clearChatMessages() {
        chatHistoryArea.setText("");
    }

    //입력칸에서 Enter를 눌러 전송하면 listener 실행
    //입력한 내용은 e.getActionCommand()로 꺼냄 (JTextField는 입력 내용을 ActionEvent에 담아 보냄)
    public void setChatEnterListener(ActionListener listener) {
        chatInputField.addActionListener(listener);
    }
}