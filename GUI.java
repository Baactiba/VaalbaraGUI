import javax.swing.*;
import javax.swing.Timer;
import javax.swing.text.*;
import java.awt.*;
import java.util.*;

public class GUI implements Runnable
{
    public static void main(String[] args) throws InterruptedException {
        SwingUtilities.invokeLater(new GUI());
//        initialize(6, 100, 4, 768);
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

    final static Object synch = new Object();

    static Timer timer = new Timer(1000 / 60, f -> updateResults());

    static StyledDocument doc = live.getStyledDocument();
    public void run() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container cont = frame.getContentPane();
        cont.setLayout(new BorderLayout());
        topPanel = new JPanel(new GridLayout(1,  3));
        midPanel = new JPanel(new BorderLayout());
        botPanel = new JPanel(new GridLayout(2, 1));
//        botPanel.add(live);
        botPanel.add(p1);
        botPanel.add(p2);
        botPanel.setPreferredSize(new Dimension(10, 150));
        p1.setMaximum(10000);
        p2.setMaximum(10000);

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
            timer.start();


            try {
                initialize(Integer.parseInt(size.getText()), Integer.parseInt(maxSum.getText()),
                        Integer.parseInt(threads.getText()), Integer.parseInt(cache.getText()));
            } catch (InterruptedException ex) { }
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
    public static void initialize(int setLength, int maxSum, int threads, int cache) throws InterruptedException {
        long sTime = System.currentTimeMillis();
        ArrayList<int[]> startingJobs = new ArrayList<>();
        for (int x = 2; x <= maxSum / setLength - setLength / 2; x++) {
            for (int y = x + 1; y <= (maxSum - x) / (setLength - 1) - (setLength - 1) / 2; y++) {
                for (int z = y + 1; z <= (maxSum - x - y) / (setLength - 2) - (setLength - 2) / 2; z++) {
                    HashSet<Integer> smallStuff = new HashSet<>();
                    smallStuff.add(x);
                    smallStuff.add(y);
                    smallStuff.add(z);
                    smallStuff.add(x + y);
                    smallStuff.add(x + z);
                    smallStuff.add(y + z);
                    smallStuff.add(x + y + z);
                    smallStuff.add(x * y);
                    smallStuff.add(x * z);
                    smallStuff.add(y * z);
                    smallStuff.add(x * y * z);
                    if (smallStuff.size() == 11)
                        startingJobs.add(new int[] {x, y, z});
                }
            }
        }
        LinkedList<Integer> tba = new LinkedList<>();
        ArrayList[] jobjobs = new ArrayList[threads];
        for (int x = 0; x < threads; x++)
            jobjobs[x] = new ArrayList<Integer>();
        for (int[] job : startingJobs) {
            if (tba.isEmpty()) {
                for (int y = 0; y < threads; y++)
                    tba.add(y);
                Collections.shuffle(tba);
            }
            int addingIndex = tba.remove(0);
            jobjobs[addingIndex].add(job);
        }
        ArrayList<Thread> runnables = new ArrayList<>();
        for (int x = 0; x < threads; x++) {
            Algorithm adding = new Algorithm(setLength, maxSum, cache, jobjobs[x]);
            runnables.add(new Thread(adding));
            algs.add(adding);
        }
        new Thread(() -> {
            for (Thread t : runnables)
                t.start();
            for (Thread t : runnables) {
                try {
                    t.join();
                } catch (InterruptedException e) { }
            }
            SwingUtilities.invokeLater(() -> {
                timer.stop();
                p1.setValue(10000);
                p2.setValue(10000);
                updateResults();
                System.out.println(System.currentTimeMillis() - sTime);
            });
        }).start();
    }
    static ArrayList<Algorithm> algs = new ArrayList<>();
    static double lp = 0;
    static int lpStrk = 0;
    public static void updateResults() {
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        synchronized(synch) {
            Collections.sort(results, Comparator.comparingInt(a -> a.score));
            for (Result r : results) {
                resultsPanel.add(r);
                r.setMaximumSize(new Dimension(Integer.MAX_VALUE, r.getPreferredSize().height));
            }
        }

        double prog = 0;
        for (Algorithm a : algs) {
            double prog1 = (double) a.completedJobs / a.jobs.size();
            try {
                int[] lj = a.jobs.get(a.completedJobs);
                int last = lj[lj.length - 1];
                double prog2 = (double) (a.indices[0] - last - 1) / (a.maxes[0] - last);
                int denom = (a.maxes[0] - last);
                for (int x = 1; x < a.maxes.length; x++) {
                    double prog3 = (double) (a.indices[x] - a.indices[x - 1] - 1) / (a.maxes[x] - a.indices[x - 1]);
                    prog2 += prog3 / denom;
                    if (a.maxes[x] == 0 || a.indices[x] == 0)
                        break;
                    denom *= (a.maxes[x] - a.indices[x - 1]);
                }
                prog1 += prog2 / a.jobs.size();
            } catch (Exception e) {}
            prog += prog1 / algs.size();
        }
        if (prog < lp) {
            lpStrk++;
            prog = lp;
        }
        else if (lpStrk >= 5) {
            lp = prog;
            lpStrk = 0;
        }
//        prog = Math.max(prog, lp);
//        lp = prog;
//        System.out.println(prog);
        p1.setValue((int) (10000 * prog));
        prog *= 100;
        prog = prog % 1;
        p2.setValue((int) (10000 * prog));
//        System.out.println(p1.getValue() + " " + p2.getValue() + "\n\n");


        JScrollPane p = new JScrollPane(resultsPanel);
        p.getVerticalScrollBar().setUnitIncrement(16);
        resultsPanel.setBackground(Color.GRAY);
        midPanel.removeAll();
        midPanel.add(p, BorderLayout.CENTER);
//        p.revalidate();
//        p.repaint();
//        resultsPanel.revalidate();
//        resultsPanel.repaint();
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