import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;

public class AgeCalculator extends JFrame {

    private JTextField dobField;
    private JLabel resultLabel;
    private DefaultListModel<String> listModel;
    private JList<String> resultList;

    public AgeCalculator() {
        setTitle("Age Calculator");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 245, 255));

        JLabel title = new JLabel("Age Calculator", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(45, 70, 140));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        centerPanel.setBackground(new Color(240, 245, 255));

        JLabel dobLabel = new JLabel("Enter Date of Birth (yyyy-MM-dd):");
        dobField = new JTextField();
        dobField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton calculateButton = new JButton("Calculate Age");
        calculateButton.setBackground(new Color(60, 100, 200));
        calculateButton.setForeground(Color.WHITE);

        resultLabel = new JLabel("Your age will appear here.");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 15));

        centerPanel.add(dobLabel);
        centerPanel.add(dobField);
        centerPanel.add(calculateButton);
        centerPanel.add(resultLabel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(
            BorderFactory.createTitledBorder("Age Details (Reorderable)")
        );

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton upButton = new JButton("Move Up");
        JButton downButton = new JButton("Move Down");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(clearButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        calculateButton.addActionListener(e -> calculateAge());

        upButton.addActionListener(e -> moveItem(-1));
        downButton.addActionListener(e -> moveItem(1));

        clearButton.addActionListener(e -> {
            dobField.setText("");
            resultLabel.setText("Your age will appear here.");
            listModel.clear();
        });
    }

    private void calculateAge() {
        try {
            LocalDate dob = LocalDate.parse(
                dobField.getText().trim(),
                DateTimeFormatter.ISO_LOCAL_DATE
            );

            LocalDate today = LocalDate.now();

            if (dob.isAfter(today)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Date of birth cannot be in the future.",
                    "Invalid Date",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Period age = Period.between(dob, today);

            resultLabel.setText(
                "Age: " + age.getYears() + " years, "
                + age.getMonths() + " months, "
                + age.getDays() + " days"
            );

            listModel.clear();
            listModel.addElement("Date of Birth: " + dob);
            listModel.addElement("Today's Date: " + today);
            listModel.addElement("Years: " + age.getYears());
            listModel.addElement("Months: " + age.getMonths());
            listModel.addElement("Days: " + age.getDays());

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Enter a valid date in yyyy-MM-dd format.\nExample: 2005-08-15",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void moveItem(int direction) {
        int index = resultList.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(
                this, "Select an item first."
            );
            return;
        }

        int newIndex = index + direction;

        if (newIndex < 0 || newIndex >= listModel.size()) {
            return;
        }

        String item = listModel.getElementAt(index);
        listModel.remove(index);
        listModel.add(newIndex, item);
        resultList.setSelectedIndex(newIndex);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AgeCalculator().setVisible(true);
        });
    }
}