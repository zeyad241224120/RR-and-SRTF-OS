import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// ═══════════════════════════════════════════════
//  FILE 6 — SchedulingProjectC2.java
//  المسؤول: الشخص السادس
//  الهدف:   الـ Main Frame الذي يجمع كل الأجزاء معاً
//            + تشغيل المحاكاة + main()
// ═══════════════════════════════════════════════

public class SchedulingProjectC2 extends JFrame {

    // --- Panels النتائج ---
    private final JPanel    rrResultPanel;
    private final JPanel    srtfResultPanel;
    private final JPanel    comparisonPanel;
    private final JTextArea conclusionArea;
    private final JTabbedPane tabbedPane;

    // --- لوحة الإدخال ---
    private final InputPanel inputPanel;

    // -----------------------------------------------
    //  Constructor — بناء الواجهة الكاملة
    // -----------------------------------------------
    public SchedulingProjectC2() {
        setTitle("OS Course Project | C2: Round Robin vs SRTF Simulator");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // إنشاء لوحة الإدخال وتمرير callback للمحاكاة
        inputPanel = new InputPanel(this::runSimulation);
        add(inputPanel, BorderLayout.NORTH);

        // إنشاء Tabs
        tabbedPane      = new JTabbedPane();
        rrResultPanel   = new JPanel(new BorderLayout());
        srtfResultPanel = new JPanel(new BorderLayout());
        comparisonPanel = new JPanel(new BorderLayout());
        conclusionArea  = new JTextArea();

        conclusionArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        conclusionArea.setEditable(false);

        // Tab 1: جدول الإدخال
        DefaultTableModel tableModel = inputPanel.getTableModel();
        tabbedPane.addTab("Workload Input",     new JScrollPane(new JTable(tableModel)));
        tabbedPane.addTab("Round Robin Results", rrResultPanel);
        tabbedPane.addTab("SRTF Results",        srtfResultPanel);
        tabbedPane.addTab("Comparison Summary",  comparisonPanel);
        tabbedPane.addTab("Analysis & Conclusion", new JScrollPane(conclusionArea));

        add(tabbedPane, BorderLayout.CENTER);
    }

    // -----------------------------------------------
    //  runSimulation — يشغّل الخوارزميتين ويعرض النتائج
    // -----------------------------------------------
    private void runSimulation() {
        List<Process> processList = inputPanel.getProcessList();

        if (processList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No processes to simulate!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // التحقق من الـ Quantum
        int q;
        try {
            q = Integer.parseInt(inputPanel.getQuantumText());
            if (q <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Time Quantum must be a positive integer (> 0)!",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // استنساخ العمليات لكل خوارزمية بشكل مستقل
        List<Process> pRR   = new ArrayList<>();
        List<Process> pSRTF = new ArrayList<>();
        for (Process p : processList) {
            pRR.add(p.cloneProcess());
            pSRTF.add(p.cloneProcess());
        }

        // ═══ تشغيل الخوارزميتين ═══
        SimulationResult rrRes   = RoundRobinAlgorithm.run(pRR, q);
        SimulationResult srtfRes = SRTFAlgorithm.run(pSRTF);

        // ═══ عرض النتائج ═══
        ResultRenderer.render(rrResultPanel,   rrRes,   "Round Robin (Quantum = " + q + ")", true);
        ResultRenderer.render(srtfResultPanel, srtfRes, "SRTF (Preemptive SJF)",             false);
        ResultRenderer.renderComparison(comparisonPanel, rrRes, srtfRes, q);
        ResultRenderer.updateConclusion(conclusionArea,  rrRes, srtfRes, q);

        // الانتقال لتاب Round Robin تلقائياً
        tabbedPane.setSelectedIndex(1);
    }

    // -----------------------------------------------
    //  main — نقطة بداية البرنامج
    // -----------------------------------------------
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new SchedulingProjectC2().setVisible(true));
    }
}