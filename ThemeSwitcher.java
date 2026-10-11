import javax.swing.*;
import java.awt.*;

public class ThemeSwitcher extends JFrame {

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel messageLabel;
    private JButton toggleButton;
    private boolean darkMode = false;

    public ThemeSwitcher() {
        setTitle("Dark / Light Theme Switcher");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        titleLabel = new JLabel("Theme Switcher", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));

        messageLabel = new JLabel(
            "Welcome! Choose your favourite theme.",
            SwingConstants.CENTER
        );
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        toggleButton = new JButton("Switch to Dark Mode");
        toggleButton.setFont(new Font("Arial", Font.BOLD, 15));
        toggleButton.setFocusPainted(false);

        JLabel statusLabel = new JLabel(
            "Current Theme: Light Mode", SwingConstants.CENTER
        );

        mainPanel.add(titleLabel);
        mainPanel.add(messageLabel);
        mainPanel.add(toggleButton);
        mainPanel.add(statusLabel);

        add(mainPanel);

        toggleButton.addActionListener(e -> {
            darkMode = !darkMode;

            if (darkMode) {
                mainPanel.setBackground(new Color(35, 35, 35));
                titleLabel.setForeground(Color.WHITE);
                messageLabel.setForeground(Color.LIGHT_GRAY);
                statusLabel.setForeground(Color.WHITE);

                toggleButton.setText("Switch to Light Mode");
                statusLabel.setText("Current Theme: Dark Mode");

                toggleButton.setBackground(new Color(80, 80, 80));
                toggleButton.setForeground(Color.WHITE);
            } else {
                mainPanel.setBackground(new Color(245, 245, 245));
                titleLabel.setForeground(new Color(35, 75, 120));
                messageLabel.setForeground(Color.DARK_GRAY);
                statusLabel.setForeground(Color.DARK_GRAY);

                toggleButton.setText("Switch to Dark Mode");
                statusLabel.setText("Current Theme: Light Mode");

                toggleButton.setBackground(new Color(220, 230, 245));
                toggleButton.setForeground(Color.BLACK);
            }

            mainPanel.repaint();
        });

        // Initial light theme
        mainPanel.setBackground(new Color(245, 245, 245));
        titleLabel.setForeground(new Color(35, 75, 120));
        messageLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setForeground(Color.DARK_GRAY);
        toggleButton.setBackground(new Color(220, 230, 245));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ThemeSwitcher().setVisible(true);
        });
    }
}
