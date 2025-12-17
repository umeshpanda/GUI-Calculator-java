package app;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class MultiToolCalculator extends JFrame {

    private final DecimalFormat moneyFmt = new DecimalFormat("#,##0.00");
    private final DecimalFormat numberFmt = new DecimalFormat("#,##0.###");

    public MultiToolCalculator() {
        super("Multi-Functional Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 520);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Income & Expense", buildIncomeExpensePanel());
        tabs.add("Unit Conversion", buildUnitConversionPanel());
        tabs.add("Percentage Toolkit", buildPercentagePanel());
        tabs.add("Electricity Bill", buildElectricityPanel());

        add(tabs);
    }

    private JPanel buildIncomeExpensePanel() {
        JTextField incomeField = new JTextField();
        JTextField expenseField = new JTextField();
        JLabel result = new JLabel("Enter values and press Calculate.");

        JButton calc = new JButton("Calculate");
        calc.addActionListener(e -> {
            try {
                double income = Double.parseDouble(incomeField.getText());
                double expenses = Double.parseDouble(expenseField.getText());
                double balance = income - expenses;
                double savingsPct = income == 0 ? 0 : (balance / income) * 100;
                result.setText(String.format(
                        "Balance: %s | Savings: %s%%",
                        moneyFmt.format(balance),
                        numberFmt.format(savingsPct)
                ));
            } catch (NumberFormatException ex) {
                showError("Please enter valid numeric values.");
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new TitledBorder("Income vs Expense"));
        panel.add(labeledField("Monthly Income:", incomeField));
        panel.add(labeledField("Monthly Expenses:", expenseField));
        panel.add(calc);
        panel.add(result);
        return wrap(panel);
    }

    private JPanel buildUnitConversionPanel() {
        String[] categories = {"Length (m↔ft)", "Weight (kg↔lb)", "Temperature (°C↔°F)"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        JTextField inputField = new JTextField();
        JLabel output = new JLabel("Result will appear here.");

        JButton convert = new JButton("Convert");
        convert.addActionListener(e -> {
            try {
                double value = Double.parseDouble(inputField.getText());
                String selected = (String) categoryBox.getSelectedItem();
                double resultValue;
                String label;
                switch (selected) {
                    case "Length (m↔ft)":
                        resultValue = value * 3.28084;
                        label = numberFmt.format(value) + " m = " + numberFmt.format(resultValue) + " ft";
                        break;
                    case "Weight (kg↔lb)":
                        resultValue = value * 2.20462;
                        label = numberFmt.format(value) + " kg = " + numberFmt.format(resultValue) + " lb";
                        break;
                    default:
                        resultValue = (value * 9 / 5) + 32;
                        label = numberFmt.format(value) + " °C = " + numberFmt.format(resultValue) + " °F";
                }
                output.setText(label);
            } catch (NumberFormatException ex) {
                showError("Please enter a valid numeric input.");
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new TitledBorder("Quick Conversions"));
        panel.add(categoryBox);
        panel.add(labeledField("Value:", inputField));
        panel.add(convert);
        panel.add(output);
        return wrap(panel);
    }

    private JPanel buildPercentagePanel() {
        JTextField baseField = new JTextField();
        JTextField changeField = new JTextField();
        JLabel result = new JLabel("Choose an action.");

        JButton increaseBtn = new JButton("Increase");
        increaseBtn.addActionListener(e -> percentageOperation(baseField, changeField, result, true));

        JButton decreaseBtn = new JButton("Decrease");
        decreaseBtn.addActionListener(e -> percentageOperation(baseField, changeField, result, false));

        JButton percentOfBtn = new JButton("% of Total");
        percentOfBtn.addActionListener(e -> {
            try {
                double pct = Double.parseDouble(changeField.getText());
                double base = Double.parseDouble(baseField.getText());
                double value = base * pct / 100;
                result.setText(numberFmt.format(pct) + "% of " + moneyFmt.format(base) + " = " + moneyFmt.format(value));
            } catch (NumberFormatException ex) {
                showError("Enter valid numbers for both fields.");
            }
        });

        JButton whatPercentBtn = new JButton("What %?");
        whatPercentBtn.addActionListener(e -> {
            try {
                double part = Double.parseDouble(baseField.getText());
                double whole = Double.parseDouble(changeField.getText());
                double pct = whole == 0 ? 0 : (part / whole) * 100;
                result.setText(moneyFmt.format(part) + " is " + numberFmt.format(pct) + "% of " + moneyFmt.format(whole));
            } catch (NumberFormatException ex) {
                showError("Enter valid numbers for both fields.");
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new TitledBorder("Percentage Toolkit"));
        panel.add(labeledField("Base / Part:", baseField));
        panel.add(labeledField("Percent / Whole:", changeField));
        panel.add(row(increaseBtn, decreaseBtn));
        panel.add(row(percentOfBtn, whatPercentBtn));
        panel.add(result);
        return wrap(panel);
    }

    private JPanel buildElectricityPanel() {
        JTextField unitsField = new JTextField();
        JTextField fixedChargeField = new JTextField("75");
        JTextField taxField = new JTextField("5");
        JLabel result = new JLabel("Enter usage details.");

        JButton calc = new JButton("Estimate Bill");
        calc.addActionListener(e -> {
            try {
                double units = Double.parseDouble(unitsField.getText());
                double fixed = Double.parseDouble(fixedChargeField.getText());
                double taxPct = Double.parseDouble(taxField.getText());

                double energyCharge = tieredCharge(units);
                double subtotal = energyCharge + fixed;
                double tax = subtotal * taxPct / 100;
                double total = subtotal + tax;

                result.setText(String.format(
                        "Energy: %s | Tax: %s | Total: %s",
                        moneyFmt.format(energyCharge),
                        moneyFmt.format(tax),
                        moneyFmt.format(total)
                ));
            } catch (NumberFormatException ex) {
                showError("Provide numeric values for units, fixed charge, and tax %.");
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new TitledBorder("Electricity Bill Estimator"));
        panel.add(labeledField("Units Consumed (kWh):", unitsField));
        panel.add(labeledField("Fixed Charges:", fixedChargeField));
        panel.add(labeledField("Tax (%):", taxField));
        panel.add(calc);
        panel.add(result);
        return wrap(panel);
    }

    private double tieredCharge(double units) {
        double remaining = units;
        double cost = 0;

        double firstTier = Math.min(remaining, 100);
        cost += firstTier * 1.5;
        remaining -= firstTier;

        if (remaining > 0) {
            double secondTier = Math.min(remaining, 200);
            cost += secondTier * 2.5;
            remaining -= secondTier;
        }

        if (remaining > 0) {
            cost += remaining * 3.75;
        }
        return cost;
    }

    private void percentageOperation(JTextField baseField, JTextField changeField, JLabel result, boolean increase) {
        try {
            double base = Double.parseDouble(baseField.getText());
            double pct = Double.parseDouble(changeField.getText());
            double delta = base * pct / 100;
            double outcome = increase ? base + delta : base - delta;
            result.setText(String.format("%s result: %s (Δ=%s)",
                    increase ? "Increase" : "Decrease",
                    moneyFmt.format(outcome),
                    moneyFmt.format(delta)
            ));
        } catch (NumberFormatException ex) {
            showError("Enter valid numbers for both fields.");
        }
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel row(JButton left, JButton right) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.add(left);
        panel.add(right);
        return panel;
    }

    private JPanel wrap(JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(component, BorderLayout.NORTH);
        wrapper.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        return wrapper;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MultiToolCalculator().setVisible(true));
    }
}