import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class SchedulingProjectC2 extends JFrame {

    // --- Process Class ---
    static class Process {
        String id;
        int arrivalTime, burstTime, remainingTime;
        int completionTime, waitingTime, turnaroundTime, responseTime;
        int firstStartTime = -1;

        public Process(String id, int arrivalTime, int burstTime) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
        }

        public Process cloneProcess() {
            return new Process(this.id, this.arrivalTime, this.burstTime);
        }
    }

    static class GanttSegment {
        String processId;
        int startTime, endTime;
        public GanttSegment(String id, int start, int end) {
            this.processId = id; this.startTime = start; this.endTime = end;
        }
    }

    static class SimulationResult {
        List<Process> procs;
        List<GanttSegment> gantt;
        List<String> queueSnapshots;
        SimulationResult(List<Process> p, List<GanttSegment> g, List<String> q) {
            this.procs = p; this.gantt = g; this.queueSnapshots = q;
        }
    }

    // --- UI Components ---
    private List<Process> processList = new ArrayList<>();
    private DefaultTableModel inputTableModel;
    private JTextField pidField, atField, btField, quantumField;
    private JTabbedPane tabbedPane;
    private JPanel rrResultPanel, srtfResultPanel, comparisonPanel;
    private JTextArea conclusionArea;

    public SchedulingProjectC2() {
        setTitle("OS Course Project | C2: Round Robin vs SRTF Simulator");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        inputTableModel = new DefaultTableModel(new String[]{"PID", "Arrival Time", "Burst Time"}, 0);
        tabbedPane.addTab("Workload Input", new JScrollPane(new JTable(inputTableModel)));

        rrResultPanel   = new JPanel(new BorderLayout());
        srtfResultPanel = new JPanel(new BorderLayout());
        comparisonPanel = new JPanel(new BorderLayout());

        tabbedPane.addTab("Round Robin Results", rrResultPanel);
        tabbedPane.addTab("SRTF Results", srtfResultPanel);
        // [NEW] Dedicated Comparison Summary tab
        tabbedPane.addTab("Comparison Summary", comparisonPanel);

        conclusionArea = new JTextArea();
        conclusionArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        conclusionArea.setEditable(false);
        tabbedPane.addTab("Analysis & Conclusion", new JScrollPane(conclusionArea));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createInputPanel() {
        JPanel mainInput = new JPanel(new BorderLayout());
        JPanel fields = new JPanel(new FlowLayout());
        fields.setBorder(BorderFactory.createTitledBorder("Add Process"));

        pidField     = new JTextField(5);
        atField      = new JTextField(5);
        btField      = new JTextField(5);
        quantumField = new JTextField("2", 3);

        fields.add(new JLabel("PID:"));     fields.add(pidField);
        fields.add(new JLabel("Arrival:")); fields.add(atField);
        fields.add(new JLabel("Burst:"));   fields.add(btField);
        fields.add(new JLabel("Quantum:")); fields.add(quantumField);

        JButton addButton   = new JButton("Add Process");
        JButton clearButton = new JButton("Clear All");
        JButton simButton   = new JButton("▶ Simulate Both");
        simButton.setBackground(new Color(46, 204, 113));
        simButton.setForeground(Color.WHITE);
        fields.add(addButton);
        fields.add(clearButton);
        fields.add(simButton);

        JPanel scenarioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        scenarioPanel.setBorder(BorderFactory.createTitledBorder("Required Test Scenarios (A-E)"));
        String[] scenarioLabels = {"A: Basic Mixed", "B: Quantum Sensitivity", "C: Short-job Heavy", "D: Interactive Fairness", "E: Validation Case"};
        for (String label : scenarioLabels) {
            JButton btn = new JButton(label);
            btn.addActionListener(e -> handleScenario(label.split(":")[0].trim()));
            scenarioPanel.add(btn);
        }

        addButton.addActionListener(e -> addProcess());

        clearButton.addActionListener(e -> {
            processList.clear();
            inputTableModel.setRowCount(0);
        });

        simButton.addActionListener(e -> runSimulation());

        mainInput.add(fields, BorderLayout.NORTH);
        mainInput.add(scenarioPanel, BorderLayout.SOUTH);
        return mainInput;
    }

    // [FIX] Separated into its own method with full validation + clear field messages
    private void addProcess() {
    try {
        String id = pidField.getText().trim();

        // Validate PID
        if (id.isEmpty())
            throw new Exception("PID cannot be empty.");

        // NEW: Prevent duplicate PID
        for (Process p : processList) {
            if (p.id.equalsIgnoreCase(id)) {
                throw new Exception("PID already exists. Use a unique PID.");
            }
        }

        int at = Integer.parseInt(atField.getText().trim());
        int bt = Integer.parseInt(btField.getText().trim());

        // Validate Arrival Time
        if (at < 0)
            throw new Exception("Arrival Time must be >= 0.");

        // Validate Burst Time
        if (bt <= 0)
            throw new Exception("Burst Time must be > 0.");

        // Add process
        processList.add(new Process(id, at, bt));
        inputTableModel.addRow(new Object[]{id, at, bt});

        // Clear fields
        pidField.setText("");
        atField.setText("");
        btField.setText("");

    } catch (NumberFormatException ex) {

        JOptionPane.showMessageDialog(
            this,
            "Invalid input! Arrival Time and Burst Time must be numeric integers.",
            "Validation Error",
            JOptionPane.ERROR_MESSAGE
        );

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Validation Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

    private void handleScenario(String type) {
        processList.clear();
        inputTableModel.setRowCount(0);

        switch (type) {
            case "A": load(new Object[][]{{"P1",0,8},{"P2",1,4},{"P3",2,9},{"P4",3,5}}, "3"); break;
            case "B": load(new Object[][]{{"P1",0,20},{"P2",0,20}}, "10"); break;
            case "C": load(new Object[][]{{"P1",0,20},{"P2",1,2},{"P3",2,2}}, "2"); break;
            case "D": load(new Object[][]{{"P1",0,10},{"P2",0,10},{"P3",0,10}}, "1"); break;

            // [FIX] Scenario E: actually triggers validation errors one by one
            case "E":
                JOptionPane.showMessageDialog(this,
                    "Scenario E — Validation Demo\n\nThree invalid entries will be attempted:\n" +
                    "  1) Negative Arrival Time  (-5)\n" +
                    "  2) Zero Burst Time  (0)\n" +
                    "  3) Empty PID\n\n" +
                    "Each will raise an error. Then a valid process is added to confirm recovery.",
                    "Scenario E: Validation Case", JOptionPane.INFORMATION_MESSAGE);

                // Attempt 1 — negative arrival
                pidField.setText("ErrP1"); atField.setText("-5"); btField.setText("5");
                addProcess();
                // Attempt 2 — zero burst
                pidField.setText("ErrP2"); atField.setText("0"); btField.setText("0");
                addProcess();
                // Attempt 3 — empty PID
                pidField.setText(""); atField.setText("1"); btField.setText("3");
                addProcess();
                // Valid recovery
                pidField.setText("ValidP"); atField.setText("0"); btField.setText("5");
                addProcess();
                return;
        }
        runSimulation();
    }

    private void load(Object[][] data, String q) {
        quantumField.setText(q);
        for (Object[] row : data) {
            processList.add(new Process((String)row[0], (int)row[1], (int)row[2]));
            inputTableModel.addRow(row);
        }
    }

    private void runSimulation() {
        if (processList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No processes to simulate!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // [FIX] Validate quantum field
        int q;
        try {
            q = Integer.parseInt(quantumField.getText().trim());
            if (q <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Time Quantum must be a positive integer (> 0)!",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Process> pRR   = new ArrayList<>();
        List<Process> pSRTF = new ArrayList<>();
        for (Process p : processList) {
            pRR.add(p.cloneProcess());
            pSRTF.add(p.cloneProcess());
        }

        SimulationResult rrRes   = runRR(pRR, q);
        SimulationResult srtfRes = runSRTF(pSRTF);

        render(rrResultPanel,   rrRes,   "Round Robin (Quantum = " + q + ")", true);
        render(srtfResultPanel, srtfRes, "SRTF (Preemptive SJF)",             false);
        renderComparison(rrRes, srtfRes, q);   // [NEW] fill Comparison tab
        updateConclusion(rrRes, srtfRes, q);   // [FIX] now shows actual numbers
        tabbedPane.setSelectedIndex(1);
    }

    // -----------------------------------------------------------------------
    // Round Robin  (captures ready-queue snapshots at every dispatch)
    // -----------------------------------------------------------------------
    private SimulationResult runRR(List<Process> procs, int q) {
        List<GanttSegment> gantt         = new ArrayList<>();
        List<String>       queueSnapshots = new ArrayList<>();  // [NEW]
        Queue<Process>     readyQueue    = new LinkedList<>();

        int time = 0, completed = 0, n = procs.size();
        procs.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int idx = 0;

        while (completed < n) {
            while (idx < n && procs.get(idx).arrivalTime <= time)
                readyQueue.add(procs.get(idx++));

            if (readyQueue.isEmpty()) {
                queueSnapshots.add("t=" + time + ": Queue = [] → CPU IDLE");
                gantt.add(new GanttSegment("IDLE", time, ++time));
                continue;
            }

            // [NEW] Snapshot before dequeue
            StringBuilder sb = new StringBuilder("t=" + time + ": Queue = [");
            List<Process> snap = new ArrayList<>(readyQueue);
            for (int i = 0; i < snap.size(); i++) {
                sb.append(snap.get(i).id)
                  .append("(rem=").append(snap.get(i).remainingTime).append(")");
                if (i < snap.size() - 1) sb.append(", ");
            }

            Process curr = readyQueue.poll();
            sb.append("] → Dispatching: ").append(curr.id);
            queueSnapshots.add(sb.toString());

            if (curr.firstStartTime == -1) {
                curr.firstStartTime = time;
                curr.responseTime   = time - curr.arrivalTime;
            }

            int burst = Math.min(curr.remainingTime, q);
            gantt.add(new GanttSegment(curr.id, time, time + burst));

            for (int i = 0; i < burst; i++) {
                time++;
                while (idx < n && procs.get(idx).arrivalTime <= time)
                    readyQueue.add(procs.get(idx++));
            }

            curr.remainingTime -= burst;
            if (curr.remainingTime > 0) {
                readyQueue.add(curr);
            } else {
                completed++;
                curr.completionTime  = time;
                curr.turnaroundTime  = time - curr.arrivalTime;
                curr.waitingTime     = curr.turnaroundTime - curr.burstTime;
            }
        }
        return new SimulationResult(procs, gantt, queueSnapshots);
    }

    // -----------------------------------------------------------------------
    // SRTF
    // -----------------------------------------------------------------------
    private SimulationResult runSRTF(List<Process> procs) {
        List<GanttSegment> gantt = new ArrayList<>();
        int time = 0, completed = 0, n = procs.size();
        String last = "";

        while (completed < n) {
            Process shortest = null; int min = Integer.MAX_VALUE;
            for (Process p : procs) {
                if (p.arrivalTime <= time && p.remainingTime > 0 && p.remainingTime < min) {
                    min = p.remainingTime; shortest = p;
                }
            }
            if (shortest == null) {
                gantt.add(new GanttSegment("IDLE", time, ++time));
                last = "IDLE"; continue;
            }
            if (shortest.firstStartTime == -1) {
                shortest.firstStartTime = time;
                shortest.responseTime   = time - shortest.arrivalTime;
            }
            if (last.equals(shortest.id)) gantt.get(gantt.size()-1).endTime++;
            else gantt.add(new GanttSegment(shortest.id, time, time + 1));

            shortest.remainingTime--; time++; last = shortest.id;
            if (shortest.remainingTime == 0) {
                completed++;
                shortest.completionTime = time;
                shortest.turnaroundTime = time - shortest.arrivalTime;
                shortest.waitingTime    = shortest.turnaroundTime - shortest.burstTime;
            }
        }
        return new SimulationResult(procs, gantt, new ArrayList<>());
    }

    // -----------------------------------------------------------------------
    // Render a single algorithm's tab
    // -----------------------------------------------------------------------
    private void render(JPanel panel, SimulationResult res, String title, boolean showQueue) {
        panel.removeAll();

        String[] cols = {"PID", "Arrival", "Burst", "Completion", "Waiting Time", "Turnaround", "Response Time"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        double sw = 0, st = 0, sr = 0;
        for (Process pr : res.procs) {
            sw += pr.waitingTime; st += pr.turnaroundTime; sr += pr.responseTime;
            m.addRow(new Object[]{pr.id, pr.arrivalTime, pr.burstTime, pr.completionTime,
                pr.waitingTime, pr.turnaroundTime, pr.responseTime});
        }
        int n = res.procs.size();
        // [FIX] Show all three averages in the header
        JLabel header = new JLabel(String.format(
            "<html><b>%s</b> &nbsp;|&nbsp; Avg WT: <b>%.2f</b> &nbsp;|&nbsp; Avg TAT: <b>%.2f</b> &nbsp;|&nbsp; Avg RT: <b>%.2f</b></html>",
            title, sw/n, st/n, sr/n));
        header.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        panel.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        JTable table  = new JTable(m);
        table.setRowHeight(24);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        // [NEW] Ready Queue View — only for Round Robin
        if (showQueue && res.queueSnapshots != null && !res.queueSnapshots.isEmpty()) {
            JTextArea queueView = new JTextArea();
            queueView.setFont(new Font("Monospaced", Font.PLAIN, 12));
            queueView.setEditable(false);
            StringBuilder sb = new StringBuilder();
            for (String snap : res.queueSnapshots) sb.append(snap).append("\n");
            queueView.setText(sb.toString());
            JScrollPane qScroll = new JScrollPane(queueView);
            qScroll.setBorder(BorderFactory.createTitledBorder("📋 Ready Queue Snapshots (Round Robin)"));
            qScroll.setPreferredSize(new Dimension(0, 160));
            center.add(qScroll, BorderLayout.SOUTH);
        }

        panel.add(center, BorderLayout.CENTER);

        // Gantt Chart
        List<GanttSegment> gantt = res.gantt;
        JPanel gp = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gantt.isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int total = gantt.get(gantt.size()-1).endTime;
                int x = 30;
                for (GanttSegment s : gantt) {
                    int w = Math.max(1, (int)((double)(s.endTime - s.startTime) / total * (getWidth() - 60)));
                    Color c = s.processId.equals("IDLE") ? Color.LIGHT_GRAY
                        : new Color(Math.abs(s.processId.hashCode()) % 160 + 60, 110, 220);
                    g2.setColor(c); g2.fillRoundRect(x, 15, w, 38, 8, 8);
                    g2.setColor(Color.BLACK); g2.drawRoundRect(x, 15, w, 38, 8, 8);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.drawString(s.processId, x + w/2 - 8, 39);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    g2.drawString(String.valueOf(s.startTime), x, 65);
                    x += w;
                }
                g2.drawString(String.valueOf(total), x - 8, 65);
            }
        };
        gp.setPreferredSize(new Dimension(800, 80));
        gp.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        panel.add(gp, BorderLayout.SOUTH);
        panel.revalidate(); panel.repaint();
    }

    // -----------------------------------------------------------------------
    // [NEW] Comparison Summary Panel — side-by-side table with winner column
    // -----------------------------------------------------------------------
    private void renderComparison(SimulationResult rr, SimulationResult srtf, int q) {
        comparisonPanel.removeAll();

        double rrWT  = rr.procs.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double rrTAT = rr.procs.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double rrRT  = rr.procs.stream().mapToInt(p -> p.responseTime).average().orElse(0);
        double sfWT  = srtf.procs.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double sfTAT = srtf.procs.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double sfRT  = srtf.procs.stream().mapToInt(p -> p.responseTime).average().orElse(0);

        String[] cols = {"Metric", "Round Robin  (Q=" + q + ")", "SRTF", "Winner ✅"};
        DefaultTableModel cm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        cm.addRow(new Object[]{"Avg Waiting Time",
            String.format("%.2f", rrWT), String.format("%.2f", sfWT),
            rrWT <= sfWT ? "Round Robin" : "SRTF"});

        cm.addRow(new Object[]{"Avg Turnaround Time",
            String.format("%.2f", rrTAT), String.format("%.2f", sfTAT),
            rrTAT <= sfTAT ? "Round Robin" : "SRTF"});

        cm.addRow(new Object[]{"Avg Response Time",
            String.format("%.2f", rrRT), String.format("%.2f", sfRT),
            rrRT <= sfRT ? "Round Robin" : "SRTF"});

        cm.addRow(new Object[]{"Fairness (starvation-free)",
            "High — time-sliced", "Low — may starve long jobs", "Round Robin"});

        cm.addRow(new Object[]{"Short-job Advantage",
            "Moderate", "Very High (optimal)", "SRTF"});

        cm.addRow(new Object[]{"Context-switch Overhead",
            "Low–Medium", "High (preempts every unit)", "Round Robin"});

        JTable compTable = new JTable(cm);
        compTable.setRowHeight(28);
        compTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        compTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel header = new JLabel("<html><h3 style='padding:6px'>📊 Side-by-Side Comparison Summary</h3></html>");
        comparisonPanel.add(header, BorderLayout.NORTH);
        comparisonPanel.add(new JScrollPane(compTable), BorderLayout.CENTER);
        comparisonPanel.revalidate(); comparisonPanel.repaint();
    }

    // -----------------------------------------------------------------------
    // [FIX] Conclusion now shows actual numbers and answers all 6 questions
    // -----------------------------------------------------------------------
    private void updateConclusion(SimulationResult rr, SimulationResult srtf, int q) {
        double rrWT  = rr.procs.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double rrTAT = rr.procs.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double rrRT  = rr.procs.stream().mapToInt(p -> p.responseTime).average().orElse(0);
        double sfWT  = srtf.procs.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double sfTAT = srtf.procs.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double sfRT  = srtf.procs.stream().mapToInt(p -> p.responseTime).average().orElse(0);

        String qEffect;
        if      (q <= 2)  qEffect = "Small quantum → high fairness + fast response, but more context-switch overhead.";
        else if (q <= 5)  qEffect = "Moderate quantum → balanced fairness and efficiency with acceptable overhead.";
        else              qEffect = "Large quantum → reduced context switching, but RR approaches FCFS (less fair).";

        conclusionArea.setText(
            "╔══════════════════════════════════════════════════╗\n" +
            "║      FINAL PROJECT CONCLUSION  (C2)             ║\n" +
            "╚══════════════════════════════════════════════════╝\n\n" +

            "── Simulation Results ────────────────────────────────\n" +
            String.format("  Round Robin (Q=%d)  →  Avg WT: %.2f  |  Avg TAT: %.2f  |  Avg RT: %.2f%n", q, rrWT, rrTAT, rrRT) +
            String.format("  SRTF                →  Avg WT: %.2f  |  Avg TAT: %.2f  |  Avg RT: %.2f%n", sfWT, sfTAT, sfRT) +

            "\n── Required Analysis Questions ───────────────────────\n\n" +

            "Q1. Which algorithm gave better average waiting time?\n" +
            String.format("    → %s  (RR: %.2f  vs  SRTF: %.2f)%n%n",
                sfWT < rrWT ? "SRTF" : "Round Robin", rrWT, sfWT) +

            "Q2. Which algorithm gave better average response time?\n" +
            String.format("    → %s  (RR: %.2f  vs  SRTF: %.2f)%n%n",
                rrRT <= sfRT ? "Round Robin" : "SRTF", rrRT, sfRT) +

            "Q3. Did Round Robin appear fairer across all processes?\n" +
            "    → Yes. RR distributes CPU time in fixed-size slices so no\n" +
            "       process is indefinitely delayed regardless of burst length.\n\n" +

            "Q4. Did SRTF complete short jobs faster?\n" +
            "    → Yes. SRTF always preempts in favour of the process with\n" +
            "       the shortest remaining time, giving short jobs optimal priority.\n\n" +

            "Q5. How did the selected quantum (" + q + ") affect Round Robin?\n" +
            "    → " + qEffect + "\n\n" +

            "Q6. Which algorithm is recommended for this workload, and why?\n" +
            String.format("    → For this workload: %s performed better on average metrics.%n",
                (rrWT + rrTAT) <= (sfWT + sfTAT) ? "Round Robin" : "SRTF") +
            "       Use Round Robin for interactive/real-time systems where\n" +
            "       fairness and predictable response matter most.\n" +
            "       Use SRTF for batch/throughput systems where minimising\n" +
            "       waiting time and turnaround time is the primary goal.\n\n" +

            "── Final Recommendation ──────────────────────────────\n" +
            "  ✔  Round Robin  → Best for fairness & response time.\n" +
            "  ✔  SRTF         → Best for throughput & minimal waiting.\n" +
            "  ⚠  SRTF may cause starvation of long processes under\n" +
            "     heavy workloads — Round Robin guarantees progress.\n"
        );
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new SchedulingProjectC2().setVisible(true));
    }
}