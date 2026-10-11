import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class DiscountCalculator extends JFrame {

    private JTextField priceField;
    private JTextField discountField;
    private JLabel discountAmountLabel;
    private JLabel finalPriceLabel;
    private JLabel savingsLabel;
    private JProgressBar progressBar;

    private final DecimalFormat df = new DecimalFormat("0.00");

    public DiscountCalculator() {
        setTitle("Discount Calculator");
        setSize(480, 530);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 245, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel title = new JLabel("Discount Calculator");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(35, 70, 140));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Calculate your savings instantly");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(25));

        mainPanel.add(createLabel("Original Price (₹):"));
        priceField = new JTextField();
        styleTextField(priceField);
        mainPanel.add(priceField);

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createLabel("Discount Percentage (%):"));

        discountField = new JTextField();
        styleTextField(discountField);
        mainPanel.add(discountField);

        mainPanel.add(Box.createVerticalStrut(20));

        JButton calculateButton = new JButton("Calculate Discount");
        calculateButton.setFont(new Font("Arial", Font.BOLD, 15));
        calculateButton.setBackground(new Color(45, 105, 210));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setMaximumSize(new Dimension(300, 42));
        calculateButton.addActionListener(e -> calculateDiscount());

        mainPanel.add(calculateButton);
        mainPanel.add(Box.createVerticalStrut(20));

        JLabel progressTitle = new JLabel("Discount Progress");
        progressTitle.setFont(new Font("Arial", Font.BOLD, 14));
        progressTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(progressTitle);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setForeground(new Color(45, 170, 100));
        progressBar.setBackground(Color.WHITE);
        progressBar.setMaximumSize(new Dimension(400, 28));

        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(progressBar);
        mainPanel.add(Box.createVerticalStrut(20));

        discountAmountLabel = createResultLabel("Discount Amount: ₹0.00");
        finalPriceLabel = createResultLabel("Final Price: ₹0.00");
        savingsLabel = createResultLabel("You save: ₹0.00");

        mainPanel.add(discountAmountLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(finalPriceLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(savingsLabel);

        add(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(400, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JLabel createResultLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(35, 70, 140));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void calculateDiscount() {
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            double discount = Double.parseDouble(discountField.getText().trim());

            if (price < 0 || discount < 0 || discount > 100) {
                JOptionPane.showMessageDialog(
                    this,
                    "Price must be non-negative and discount must be between 0 and 100.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            double discountAmount = price * discount / 100;
            double finalPrice = price - discountAmount;

            discountAmountLabel.setText(
                "Discount Amount: ₹" + df.format(discountAmount)
            );

            finalPriceLabel.setText(
                "Final Price: ₹" + df.format(finalPrice)
            );

            savingsLabel.setText(
                "You save: ₹" + df.format(discountAmount)
            );

            int percentage = (int) Math.round(discount);
            progressBar.setValue(percentage);
            progressBar.setString(percentage + "% discount");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter valid numbers for price and discount.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DiscountCalculator().setVisible(true);
        });
    }
}