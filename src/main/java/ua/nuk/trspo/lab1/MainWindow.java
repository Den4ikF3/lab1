package ua.nuk.trspo.lab1;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class MainWindow extends JFrame {

    private static final double A = Benchmark.A;
    private static final double B = Benchmark.B;

    private final JTextField nField = new JTextField("10000000", 12);
    private final JSpinner threadsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
    private final JLabel resultLabel = new JLabel("—");
    private final JLabel errorLabel = new JLabel("—");
    private final JLabel timeLabel = new JLabel("—");
    private final JTextArea log = new JTextArea(16, 60);
    private final JButton calcButton = new JButton("Обчислити");
    private final JButton benchButton = new JButton("Таблиця для 1–20 потоків");

    public MainWindow() {
        super("ЛР1: ∫ ln(t+1) dt на [0;1], метод правих прямокутників");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        form.add(new JLabel("Кількість інтервалів розбиття n:"));
        form.add(nField);
        form.add(new JLabel("Кількість потоків (1–20):"));
        form.add(threadsSpinner);
        form.add(new JLabel("Значення інтеграла:"));
        form.add(resultLabel);
        form.add(new JLabel("Похибка (|результат − точне|):"));
        form.add(errorLabel);
        form.add(new JLabel("Час виконання:"));
        form.add(timeLabel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(calcButton);
        buttons.add(benchButton);

        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(log), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);

        calcButton.addActionListener(e -> calculate());
        benchButton.addActionListener(e -> benchmark());
    }

    private void calculate() {
        final long n;
        try {
            n = Long.parseLong(nField.getText().trim());
            if (n < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "n має бути цілим числом >= 1", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final int threads = (Integer) threadsSpinner.getValue();

        setBusy(true);
        new SwingWorker<double[], Void>() {
            @Override
            protected double[] doInBackground() {
                long t0 = System.nanoTime();
                double value = new IntegralCalculator(threads).calculate(A, B, n, new Function());
                double ms = (System.nanoTime() - t0) / 1e6;
                return new double[]{value, ms};
            }

            @Override
            protected void done() {
                try {
                    double[] r = get();
                    resultLabel.setText(String.format(Locale.ROOT, "%.10f", r[0]));
                    errorLabel.setText(String.format(Locale.ROOT, "%.3e", Math.abs(r[0] - Benchmark.EXACT)));
                    timeLabel.setText(String.format(Locale.ROOT, "%.3f мс", r[1]));
                    log.append(String.format(Locale.ROOT, "n=%d, потоків=%d: I=%.10f, час=%.3f мс%n", n, threads, r[0], r[1]));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setBusy(false);
                }
            }
        }.execute();
    }

    private void benchmark() {
        long parsed;
        try {
            parsed = Long.parseLong(nField.getText().trim());
        } catch (NumberFormatException ex) {
            parsed = 0;
        }
        final long large = parsed > 1_000_000 ? parsed : 10_000_000;

        setBusy(true);
        log.append("\n");
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                Benchmark.run(500, large, 20, this::publish);
                return null;
            }

            @Override
            protected void process(List<String> lines) {
                for (String s : lines) {
                    log.append(s + "\n");
                }
            }

            @Override
            protected void done() {
                setBusy(false);
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        calcButton.setEnabled(!busy);
        benchButton.setEnabled(!busy);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
