
import javax.swing.*;
import java.awt.*;

public class CurrencyConverter extends JFrame {

    JTextField amountField;
    JComboBox<String> fromCurrency, toCurrency;
    JLabel resultLabel;

    CurrencyConverter() {

        setTitle("Currency Converter");
        setSize(450, 430);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(230, 240, 255));

        // Heading
        JLabel title = new JLabel("Currency Converter");
        title.setBounds(85, 20, 300, 35);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(30, 70, 140));
        panel.add(title);

        // Amount label
        JLabel amountLabel = new JLabel("Enter Amount:");
        amountLabel.setBounds(40, 80, 150, 25);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(amountLabel);

        // Amount input
        amountField = new JTextField();
        amountField.setBounds(190, 80, 200, 30);
        amountField.setFont(new Font("Arial", Font.PLAIN, 15));
        panel.add(amountField);

        // From currency
        JLabel fromLabel = new JLabel("From Currency:");
        fromLabel.setBounds(40, 135, 150, 25);
        fromLabel.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(fromLabel);

        String[] currencies = {
            "INR", "USD", "EUR", "GBP", "JPY"
        };

        fromCurrency = new JComboBox<>(currencies);
        fromCurrency.setBounds(190, 135, 200, 30);
        panel.add(fromCurrency);

        // To currency
        JLabel toLabel = new JLabel("To Currency:");
        toLabel.setBounds(40, 185, 150, 25);
        toLabel.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(toLabel);

        toCurrency = new JComboBox<>(currencies);
        toCurrency.setBounds(190, 185, 200, 30);
        toCurrency.setSelectedItem("USD");
        panel.add(toCurrency);

        // Convert button
        JButton convertButton = new JButton("Convert");
        convertButton.setBounds(70, 245, 130, 40);
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBackground(new Color(40, 100, 200));
        convertButton.setForeground(Color.WHITE);
        panel.add(convertButton);

        // Clear button
        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(230, 245, 130, 40);
        clearButton.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(clearButton);

        // Result label
        resultLabel = new JLabel("Converted amount will appear here");
        resultLabel.setBounds(25, 310, 400, 35);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(new Color(20, 100, 60));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(resultLabel);

        // Convert button action
        convertButton.addActionListener(e -> convertCurrency());

        // Clear button action
        clearButton.addActionListener(e -> {
            amountField.setText("");
            fromCurrency.setSelectedIndex(0);
            toCurrency.setSelectedIndex(1);
            resultLabel.setText("Converted amount will appear here");
        });

        add(panel);
        setVisible(true);
    }

    void convertCurrency() {

        try {
            double amount = Double.parseDouble(
                amountField.getText()
            );

            if (amount < 0 || Double.isInfinite(amount)
                    || Double.isNaN(amount)) {
                JOptionPane.showMessageDialog(
                    this, "Enter a valid positive amount."
                );
                return;
            }

            String from = (String) fromCurrency.getSelectedItem();
            String to = (String) toCurrency.getSelectedItem();

            // Fixed rates relative to INR
            double inrRate = 1;

            if (from.equals("USD")) {
                inrRate = 88.50;
            } else if (from.equals("EUR")) {
                inrRate = 102.00;
            } else if (from.equals("GBP")) {
                inrRate = 118.00;
            } else if (from.equals("JPY")) {
                inrRate = 0.59;
            }

            double amountInINR = amount * inrRate;

            double toRate = 1;

            if (to.equals("USD")) {
                toRate = 88.50;
            } else if (to.equals("EUR")) {
                toRate = 102.00;
            } else if (to.equals("GBP")) {
                toRate = 118.00;
            } else if (to.equals("JPY")) {
                toRate = 0.59;
            }

            double result = amountInINR / toRate;

            resultLabel.setText(
                String.format("%.2f %s", result, to)
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid amount.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CurrencyConverter());
    }
}
