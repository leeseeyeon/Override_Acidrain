package client.lobby.panel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import client.lobby.ChatMessage;

public class ChatPanel extends JPanel {
    private static final Color CHAT_BACKGROUND_COLOR = new Color(30, 41, 59);    
    private static final Color CHAT_TEXT_COLOR = Color.WHITE;                    
    private static final Color SYSTEM_TEXT_COLOR = new Color(125, 180, 255);     
    private static final Color HEADER_COLOR = new Color(248, 250, 252);           
    private static final Color HEADER_BORDER_COLOR = new Color(200, 208, 220);    
    
    private JLabel chatTitleLabel;
    private JScrollPane chatHistoryScrollPane;
    private JTextPane chatHistoryArea;
    private JTextField chatInputField;

    public ChatPanel() {
        setUpPanel();
        createChatTitle();
        createChatHistory();
        createChatInputField();
    }

    private void setUpPanel() {
        setBackground(CHAT_BACKGROUND_COLOR);
        setPreferredSize(new Dimension(920, 170));
        setLayout(null);
    }

    private void createChatTitle() {
        chatTitleLabel = new JLabel(" 채팅");
        chatTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        chatTitleLabel.setOpaque(true); 
        chatTitleLabel.setBackground(HEADER_COLOR);
        chatTitleLabel.setBorder(BorderFactory.createLineBorder(HEADER_BORDER_COLOR, 1));
        chatTitleLabel.setBounds(0, 0, 920, 24);
        add(chatTitleLabel);
    }

    private void createChatHistory() {
        chatHistoryScrollPane = new JScrollPane();
        chatHistoryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatHistoryScrollPane.getViewport().setBackground(CHAT_BACKGROUND_COLOR);
        chatHistoryScrollPane.setBounds(16, 30, 888, 88);
        add(chatHistoryScrollPane);

        chatHistoryArea = new JTextPane(); 
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setBackground(CHAT_BACKGROUND_COLOR);
        chatHistoryArea.setForeground(CHAT_TEXT_COLOR); 
        chatHistoryArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatHistoryScrollPane.setViewportView(chatHistoryArea);
    }

    private void createChatInputField() {
        chatInputField = new JTextField();
        chatInputField.setToolTipText("메시지를 입력하고 Enter를 누르세요");
        chatInputField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatInputField.setBounds(16, 124, 888, 34);
        add(chatInputField);

        // Enter 후 입력칸 비우기
        chatInputField.addActionListener(e -> chatInputField.setText(""));
    }


//LobbyView 사용 메소드
    public void appendChatMessage(ChatMessage message) {
    	if (message.isSystemMessage()) {
    	    appendLine("[알림] " + message.getContent(), SYSTEM_TEXT_COLOR, true);
    	} else {
    	    appendLine(message.getSenderNickname() + " : " + message.getContent(), CHAT_TEXT_COLOR, false);  
    	}
        chatHistoryArea.setCaretPosition(chatHistoryArea.getDocument().getLength());
    }

    public void clearChatMessages() {
        chatHistoryArea.setText("");
    }

    private void appendLine(String text, Color color, boolean isBold) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, true); 
        StyledDocument document = chatHistoryArea.getStyledDocument();
        try {
            document.insertString(document.getLength(), text + "\n", style);
        } catch (BadLocationException e) { 
            e.printStackTrace();
        }
    }

    public void setChatEnterListener(ActionListener listener) {
        chatInputField.addActionListener(listener);
    }
}