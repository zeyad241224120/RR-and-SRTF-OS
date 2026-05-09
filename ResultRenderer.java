import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResultRenderer {

   
    public static void render(JPanel panel, SimulationResult res, String title, boolean showQueue) {
        panel.removeAll();

       
        String[] cols = {"PID", "Arrival", "Burst", "Completion", "Waiting Time", "Turnaround", "Response Time"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        double sw = 0, st = 0, sr = 0;

        for (Process pr : res.procs) {
            sw += pr.waitingTime;
            st += pr.turnaroundTime;
            sr += pr.responseTime;
            m.addRow(new Object[]{pr.id, pr.arrivalTime, pr.burstTime, pr.completionTime,
                pr.waitingTime, pr.turnaroundTime, pr.responseTime});
        }

        int n = res.procs.size();

        
        JLabel header = new JLabel(String.format(
            "<html><b>%s</b> &nbsp;|&nbsp; Avg WT: <b>%.2f</b> &nbsp;|&nbsp; Avg TAT: <b>%.2f</b> &nbsp;|&nbsp; Avg RT: <b>%.2f</b></html>",
            title, sw / n, st / n, sr / n));
        header.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        panel.add(header, BorderLayout.NORTH);

       
        JPanel center = new JPanel(new BorderLayout());
        JTable table  = new JTable(m);
        table.setRowHeight(24);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

       
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

        // --- Gantt Chart ---
        List<GanttSegment> gantt = res.gantt;
        JPanel gp = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gantt.isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int total = gantt.get(gantt.size() - 1).endTime;
                int x     = 30;

                for (GanttSegment s : gantt) {
                    int w = Math.max(1, (int) ((double)(s.endTime - s.startTime) / total * (getWidth() - 60)));
                    Color c = s.processId.equals("IDLE") ? Color.LIGHT_GRAY
                        : new Color(Math.abs(s.processId.hashCode()) % 160 + 60, 110, 220);

                    g2.setColor(c);     g2.fillRoundRect(x, 15, w, 38, 8, 8);
                    g2.setColor(Color.BLACK); g2.drawRoundRect(x, 15, w, 38, 8, 8);

                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.drawString(s.processId, x + w / 2 - 8, 39);

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

        panel.revalidate();
        panel.repaint();
    }

    
    // view table compare between Algorithms 
    
    public static void renderComparison(JPanel comparisonPanel, SimulationResult rr, SimulationResult srtf, int q) {
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
        comparisonPanel.revalidate();
        comparisonPanel.repaint();
    }

    
    // final  Analysis and conclusion 
    public static void updateConclusion(JTextArea conclusionArea, SimulationResult rr, SimulationResult srtf, int q) {
        double rrWT  = rr.procs.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double rrTAT = rr.procs.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double rrRT  = rr.procs.stream().mapToInt(p -> p.responseTime).average().orElse(0);
        double sfWT  = srtf.procs.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double sfTAT = srtf.procs.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double sfRT  = srtf.procs.stream().mapToInt(p -> p.responseTime).average().orElse(0);

        String qEffect;
        if      (q <= 2) qEffect = "Small quantum → high fairness + fast response, but more context-switch overhead.";
        else if (q <= 5) qEffect = "Moderate quantum → balanced fairness and efficiency with acceptable overhead.";
        else             qEffect = "Large quantum → reduced context switching, but RR approaches FCFS (less fair).";

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
}
