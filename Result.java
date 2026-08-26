import javax.swing.*;
import java.awt.*;

public class Result extends JPanel {
    static int setSize = 10;
    static int min = (int) Math.pow(2, setSize) + (int) Math.ceil(Math.min(0.125 * setSize * setSize, Math.pow(2, 0.5 * setSize)));
    static int max = 1290;
    static void recalculate() {
        max = (int) Math.pow(2, setSize);
        min = (int) Math.pow(2, setSize) + (int) Math.ceil(Math.min(0.125 * setSize * setSize, Math.pow(2, 0.5 * setSize)));
    }
    int score;
    String set;
    JTextArea textArea;
    JProgressBar progressBar;
    public Result(String set, int score) {
        this.score = score;
        this.set = set;
        GridLayout l = new GridLayout(1, 2, 5, 5);
        setBackground(Color.GRAY);
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setText(set);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        textArea.setSize(900, 100);
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(400, 30));
        progressBar.setStringPainted(true);
        progressBar.setString("" + score);
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
        progressBar.setValue(score);
        progressBar.setFont(new Font("Monospaced", Font.PLAIN, 17));
        add(textArea);
        add(progressBar);
    }
}