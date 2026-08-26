import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.*;

public class GUI implements Runnable
{
    public static void main(String[] args) throws InterruptedException {
        GUI g;
        SwingUtilities.invokeLater(g = new GUI());
        Thread.sleep(2500);
        results.add(new Result("[33, 41, 43, 44, 45, 49, 65, 131, 262, 524]", 1237));
        results.add(new Result("[32, 35, 36, 38, 40, 48, 64, 128, 288, 544]", 1253));
        results.add(new Result("[34, 39, 42, 44, 46, 50, 66, 134, 268, 536]", 1259));
        results.add(new Result("[33, 41, 43, 44, 45, 49, 65, 131, 262, 524]", 1237));
        results.add(new Result("[32, 35, 36, 38, 40, 48, 64, 128, 288, 544]", 1253));
        results.add(new Result("[34, 39, 42, 44, 46, 50, 66, 134, 268, 536]", 1259));
        results.add(new Result("[33, 41, 43, 44, 45, 49, 65, 131, 262, 524]", 1237));
        results.add(new Result("[32, 35, 36, 38, 40, 48, 64, 128, 288, 544]", 1253));
        results.add(new Result("[34, 39, 42, 44, 46, 50, 66, 134, 268, 536]", 1259));
        results.add(new Result("[33, 41, 43, 44, 45, 49, 65, 131, 262, 524]", 1237));
        results.add(new Result("[32, 35, 36, 38, 40, 48, 64, 128, 288, 544]", 1253));
        results.add(new Result("[34, 39, 42, 44, 46, 50, 66, 134, 268, 536]", 1259));
        results.add(new Result("[33, 41, 43, 44, 45, 49, 65, 131, 262, 524]", 1237));
        results.add(new Result("[32, 35, 36, 38, 40, 48, 64, 128, 288, 544]", 1253));
        results.add(new Result("[34, 39, 42, 44, 46, 50, 66, 134, 268, 536]", 1259));
        results.add(new Result("[33, 41, 43, 44, 45, 49, 65, 131, 262, 524]", 1237));
        results.add(new Result("[32, 35, 36, 38, 40, 48, 64, 128, 288, 544]", 1253));
        results.add(new Result("[34, 39, 42, 44, 46, 50, 66, 134, 268, 536]", 1259));
        results.add(new Result("[33, 41, 43, 44, 45, 49, 65, 131, 262, 524]", 1237));
        results.add(new Result("[32, 35, 36, 38, 40, 48, 64, 128, 288, 544]", 1253));
        results.add(new Result("[34, 39, 42, 44, 46, 50, 66, 134, 268, 536]", 1259));
        results.add(new Result("[33, 41, 43, 44, 45, 49, 65, 131, 262, 524]", 1237));
        results.add(new Result("[32, 35, 36, 38, 40, 48, 64, 128, 288, 544]", 1253));
        results.add(new Result("[34, 39, 42, 44, 46, 50, 66, 134, 268, 536]", 1259));
        SwingUtilities.invokeLater(g::updateResults);
    }


    static JTextPane live = new JTextPane();
    static JProgressBar p1 = new JProgressBar();
    static JProgressBar p2 = new JProgressBar();

    static JTextField size = new JTextField("Set Size:");
    static JTextField maxSum = new JTextField("Max Sum:");
    static JTextField threads = new JTextField("Threads:");
    static JTextField cache = new JTextField("L2 Cache per Core (KB):");

    static {
        live.setEditable(false);
        live.setFont(new Font("Monospaced", Font.PLAIN, 32));
        p1.setString("Progress");
        p2.setString("Sub-progress (current percent)");
        p1.setStringPainted(true);
        p2.setStringPainted(true);
    }

    static JPanel topPanel;
    static JPanel midPanel;
    static JPanel botPanel;
    static ArrayList<Result> results = new ArrayList<>();

    static StyledDocument doc = live.getStyledDocument();
    public void run() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container cont = frame.getContentPane();
        cont.setLayout(new BorderLayout());
        topPanel = new JPanel(new GridLayout(1,  3));
        midPanel = new JPanel(new BorderLayout());
        botPanel = new JPanel(new GridLayout(3, 1));
        botPanel.add(live);
        botPanel.add(p1);
        botPanel.add(p2);
        p1.setMaximum(100);
        p1.setValue(50);

        topPanel.add(size);
        topPanel.add(maxSum);
        topPanel.add(threads);
        topPanel.add(cache);

        JButton button = new JButton("Go");
        button.setFont(new Font("Monospaced", Font.PLAIN, 512));
        button.addActionListener(e -> {
            midPanel.removeAll();
            midPanel.revalidate();
            midPanel.repaint();
        });
        midPanel.add(button);

        cont.add(topPanel, BorderLayout.NORTH);
        cont.add(midPanel, BorderLayout.CENTER);
        cont.add(botPanel, BorderLayout.SOUTH);
        frame.setSize(1400, 700);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Baactiba's Vaalbara Set Finder");
        frame.setVisible(true);
        setText("User initializing search.");
    }
    public void updateResults() {
        midPanel.removeAll();
        Collections.sort(results, Comparator.comparingInt(a -> a.score));
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        for (Result r : results) {
            resultsPanel.add(r);
            r.setMaximumSize(new Dimension(Integer.MAX_VALUE, r.getPreferredSize().height));
        }
        JScrollPane p = new JScrollPane(resultsPanel);
        p.getVerticalScrollBar().setUnitIncrement(16);
        midPanel.add(p, BorderLayout.CENTER);
        resultsPanel.setBackground(Color.GRAY);
        midPanel.revalidate();
        midPanel.repaint();
    }
    public void setText(String text) {
        live.setText("");
        try {
            doc.insertString(0, "STATUS: " + text, null);
        } catch (Exception e) {}
    }
}