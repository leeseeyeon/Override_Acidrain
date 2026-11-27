package client.lobby.panel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import client.lobby.ChatMessage;

//채팅 영역 (View 부품) - 입력칸에서 Enter를 누르면 전송

public class ChatPanel extends JPanel {
    private static final Color CHAT_BACKGROUND_COLOR = new Color(30, 41, 59);    // 채팅 영역: 진한 남색
    private static final Color CHAT_TEXT_COLOR = Color.WHITE;                     // 채팅 글자: 흰색
    private static final Color SYSTEM_TEXT_COLOR = new Color(125, 180, 255);      // [알림] 글자: 하늘색
    private static final Color HEADER_COLOR = new Color(248, 250, 252);           // 위쪽 "채팅" 띠
    private static final Color HEADER_BORDER_COLOR = new Color(200, 208, 220);    // "채팅" 띠 테두리

    private JLabel chatTitleLabel;
    private JScrollPane chatHistoryScrollPane;
    private JTextPane chatHistoryArea;   // 줄마다 색을 다르게 하려고 JTextArea 대신 JTextPane 사용
    private JTextField chatInputField;

    public ChatPanel() {
        setBackground(CHAT_BACKGROUND_COLOR);
        setPreferredSize(new Dimension(920, 170));
        setLayout(null);

        chatTitleLabel = new JLabel(" 채팅");   // 앞 공백: 왼쪽 여백
        chatTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        chatTitleLabel.setOpaque(true);         // 배경색을 칠해서 제목 띠로 사용
        chatTitleLabel.setBackground(HEADER_COLOR);
        chatTitleLabel.setBorder(BorderFactory.createLineBorder(HEADER_BORDER_COLOR, 1));
        chatTitleLabel.setBounds(0, 0, 920, 24);
        add(chatTitleLabel);

        chatHistoryScrollPane = new JScrollPane();
        chatHistoryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatHistoryScrollPane.getViewport().setBackground(CHAT_BACKGROUND_COLOR);
        chatHistoryScrollPane.setBounds(16, 30, 888, 88);
        add(chatHistoryScrollPane);

        chatHistoryArea = new JTextPane();   // 긴 줄은 자동으로 아래로 넘어감
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setBackground(CHAT_BACKGROUND_COLOR);
        chatHistoryArea.setForeground(CHAT_TEXT_COLOR);   // 채팅 글자 흰색
        chatHistoryArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatHistoryScrollPane.setViewportView(chatHistoryArea);

        chatInputField = new JTextField();
        chatInputField.setToolTipText("메시지를 입력하고 Enter를 누르세요");
        chatInputField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatInputField.setBounds(16, 124, 888, 34);
        add(chatInputField);

        // Enter 후 입력칸 비우기 (입력한 내용은 ActionEvent에 이미 담겨 있어서 먼저 비워도 ok.)
        chatInputField.addActionListener(e -> chatInputField.setText(""));
    }


// ===== LobbyView가 쓰는 메소드 (유지) =====

    //채팅 한 줄을 맨 아래에 추가
    public void appendChatMessage(ChatMessage message) {
    	if (message.isSystemMessage()) {
    	    appendLine("[알림] " + message.getContent(), SYSTEM_TEXT_COLOR, true);    // 알림: 하늘색 + 굵게
    	} else {
    	    appendLine(message.getSenderNickname() + " : " + message.getContent(), CHAT_TEXT_COLOR, false);   // 채팅: 흰색, 보통
    	}
        chatHistoryArea.setCaretPosition(chatHistoryArea.getDocument().getLength());  // 맨 아래로 스크롤
    }

    //채팅창 비우기 (방에 새로 들어왔을 때)
    public void clearChatMessages() {
        chatHistoryArea.setText("");
    }

    //한 줄을 지정한 색으로 맨 아래에 붙임
    private void appendLine(String text, Color color, boolean isBold) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, true); 
        StyledDocument document = chatHistoryArea.getStyledDocument();
        try {
            document.insertString(document.getLength(), text + "\n", style);
        } catch (BadLocationException e) {   // 맨 끝에 붙이므로 일어나지 않음
            e.printStackTrace();
        }
    }

    //입력칸에서 Enter를 눌러 전송하면 listener 실행
    //입력한 내용은 e.getActionCommand()로 꺼냄 (JTextField는 입력 내용을 ActionEvent에 담아 보냄)
    public void setChatEnterListener(ActionListener listener) {
        chatInputField.addActionListener(listener);
    }
}