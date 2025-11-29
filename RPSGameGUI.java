import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class RPSGameGUI extends JFrame {
    // --- GUI Components ---
    private JTextField nameField, hostField, portField;
    private JTextArea gameLog;
    private JButton connectBtn, rockBtn, paperBtn, scissorsBtn, quitBtn;
    private JLabel statusLabel, winLabel, loseLabel, tieLabel;

    // --- Networking ---
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    // --- Game Statistics ---
    private int wins = 0, losses = 0, ties = 0;

    public RPSGameGUI() {
        // === Main Window Setup ===
        setTitle("🎮 Rock–Paper–Scissors (TCP Client)");
        setSize(620, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // === Title + Status Header ===
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(30, 30, 30));

        JLabel titleLabel = new JLabel("Rock–Paper–Scissors Game", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(titleLabel);

        statusLabel = new JLabel("🔴 Disconnected", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerPanel.add(statusLabel);

        add(headerPanel, BorderLayout.NORTH);

        // === Connection Controls ===
        JPanel connectionPanel = new JPanel();
        connectionPanel.setBackground(new Color(240, 240, 240));
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection Settings"));

        connectionPanel.add(new JLabel("Name:"));
        nameField = new JTextField("Player1", 8);
        connectionPanel.add(nameField);

        connectionPanel.add(new JLabel("Host:"));
        hostField = new JTextField("127.0.0.1", 8);
        connectionPanel.add(hostField);

        connectionPanel.add(new JLabel("Port:"));
        portField = new JTextField("12345", 5);
        connectionPanel.add(portField);

        connectBtn = new JButton("Connect");
        connectBtn.setBackground(new Color(0, 120, 215));
        connectBtn.setForeground(Color.WHITE);
        connectBtn.setFocusPainted(false);
        connectionPanel.add(connectBtn);

        add(connectionPanel, BorderLayout.SOUTH);

        // === Game Log ===
        gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        gameLog.setBorder(BorderFactory.createTitledBorder("Game Log"));
        JScrollPane scrollPane = new JScrollPane(gameLog);
        add(scrollPane, BorderLayout.CENTER);

        // === Control Buttons + Scoreboard ===
        JPanel controlPanel = new JPanel(new BorderLayout());

        // --- Game Buttons ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Select Your Move"));

        rockBtn = new JButton("🪨 Rock");
        paperBtn = new JButton("📄 Paper");
        scissorsBtn = new JButton("✂️ Scissors");
        quitBtn = new JButton("🚪 Quit");

        JButton[] buttons = {rockBtn, paperBtn, scissorsBtn, quitBtn};
        for (JButton btn : buttons) {
            btn.setPreferredSize(new Dimension(120, 40));
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setEnabled(false);
            buttonPanel.add(btn);
        }

        controlPanel.add(buttonPanel, BorderLayout.CENTER);

        // --- Scoreboard ---
        JPanel scorePanel = new JPanel();
        scorePanel.setBorder(BorderFactory.createTitledBorder("Scoreboard"));

        winLabel = new JLabel("Wins: 0");
        loseLabel = new JLabel("Losses: 0");
        tieLabel = new JLabel("Ties: 0");

        Font scoreFont = new Font("SansSerif", Font.BOLD, 14);
        winLabel.setFont(scoreFont);
        loseLabel.setFont(scoreFont);
        tieLabel.setFont(scoreFont);

        winLabel.setForeground(new Color(0, 153, 0));
        loseLabel.setForeground(Color.RED);
        tieLabel.setForeground(new Color(0, 102, 204));

        scorePanel.add(winLabel);
        scorePanel.add(loseLabel);
        scorePanel.add(tieLabel);

        controlPanel.add(scorePanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.NORTH);

        // === Button Actions ===
        connectBtn.addActionListener(e -> connectToServer());
        rockBtn.addActionListener(e -> sendMove("1"));
        paperBtn.addActionListener(e -> sendMove("2"));
        scissorsBtn.addActionListener(e -> sendMove("3"));
        quitBtn.addActionListener(e -> sendMove("Q"));
    }

    /**
     * Connect to the server using the provided host and port.
     */
    private void connectToServer() {
        try {
            String host = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            socket = new Socket(host, port);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            gameLog.append("✅ Connected to server at " + host + ":" + port + "\n");
            statusLabel.setText("🟢 Connected");
            statusLabel.setForeground(new Color(0, 180, 0));
            connectBtn.setEnabled(false);
            setGameButtonsEnabled(true);

            // Send player name
            output.println(nameField.getText().trim());

            // Start listener thread
            new Thread(this::listenForMessages).start();

        } catch (IOException e) {
            gameLog.append("❌ Connection failed: " + e.getMessage() + "\n");
        }
    }

    /**
     * Enable or disable all game action buttons.
     */
    private void setGameButtonsEnabled(boolean enabled) {
        rockBtn.setEnabled(enabled);
        paperBtn.setEnabled(enabled);
        scissorsBtn.setEnabled(enabled);
        quitBtn.setEnabled(enabled);
    }

    /**
     * Send a move (1=Rock, 2=Paper, 3=Scissors, Q=Quit) to the server.
     */
    private void sendMove(String move) {
        if (output == null) {
            gameLog.append("⚠️ Not connected to a server.\n");
            return;
        }

        output.println(move);

        if (move.equalsIgnoreCase("Q")) {
            try {
                socket.close();
                gameLog.append("👋 Disconnected from server.\n");
                statusLabel.setText("🔴 Disconnected");
                statusLabel.setForeground(Color.RED);
                setGameButtonsEnabled(false);
                connectBtn.setEnabled(true);
            } catch (IOException ignored) {}
        }
    }

    /**
     * Listen for messages from the server on a separate thread.
     */
    private void listenForMessages() {
        try {
            String msg;
            while ((msg = input.readLine()) != null) {
                final String message = msg;
                SwingUtilities.invokeLater(() -> handleServerMessage(message));
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                gameLog.append("⚠️ Connection lost.\n");
                statusLabel.setText("🔴 Disconnected");
                statusLabel.setForeground(Color.RED);
                setGameButtonsEnabled(false);
                connectBtn.setEnabled(true);
            });
        }
    }

    /**
     * Process and display server messages, and update scoreboard.
     */
    private void handleServerMessage(String msg) {
        gameLog.append(msg + "\n");

        String lower = msg.toLowerCase();
        if (lower.contains("win")) wins++;
        else if (lower.contains("lose")) losses++;
        else if (lower.contains("tie")) ties++;

        updateScoreboard();
    }

    /**
     * Refresh scoreboard labels.
     */
    private void updateScoreboard() {
        winLabel.setText("Wins: " + wins);
        loseLabel.setText("Losses: " + losses);
        tieLabel.setText("Ties: " + ties);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RPSGameGUI().setVisible(true));
    }
}