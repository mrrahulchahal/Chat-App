import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {
    private JTextPane messageArea;
    private JTextField textField;
    private JButton exitButton;
    private ChatClient client;

    public ChatClientGUI() {
        super("Chat Application");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.BLACK);

        messageArea = new JTextPane();
        messageArea.setEditable(false);
        messageArea.setBackground(Color.BLACK);
        messageArea.setForeground(Color.WHITE);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);

        String name = JOptionPane.showInputDialog(this, "Enter your name:", "Name Entry", JOptionPane.PLAIN_MESSAGE);
        this.setTitle("Chat Application - " + name);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(Color.BLACK);

        textField = new JTextField();
        textField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        textField.setForeground(Color.WHITE);
        textField.setBackground(Color.BLACK);
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + name + ": "
                        + textField.getText();
                client.sendMessage(message);
                textField.setText("");
            }
        });
        bottomPanel.add(textField, BorderLayout.CENTER);

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> {
            String departureMessage = name + " has left the chat.";
            client.sendMessage(departureMessage);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            System.exit(0);
        });
        bottomPanel.add(exitButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        try {
            this.client = new ChatClient("127.0.0.1", 5000, this::onMessageReceived);
            client.sendMessage("join " + name);
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setVisible(true);
    }

    private void onMessageReceived(String message) {
        StyledDocument doc = messageArea.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        if (message.endsWith(" has joined the chat.")) {
            StyleConstants.setForeground(attrs, Color.GREEN);
        } else if (message.endsWith("has left the chat.")) {
            StyleConstants.setForeground(attrs, Color.RED);
        } else {
            StyleConstants.setForeground(attrs, Color.WHITE);
        }
        try {
            doc.insertString(doc.getLength(), message + "\n", attrs);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChatClientGUI();
            }
        });
    }
}