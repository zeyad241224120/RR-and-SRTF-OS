# 🖥️ OS Scheduling Simulator — Round Robin vs SRTF

> **Course Project — C2**  
> A Java Swing desktop application that simulates and compares two CPU scheduling algorithms: **Round Robin (RR)** and **Shortest Remaining Time First (SRTF)**, with live Gantt charts, ready-queue snapshots, and a detailed analysis report.

---

## 📸 Screenshots

> _Add your screenshots here after running the application._

### Workload Input
<!-- Replace the line below with your screenshot -->
![Workload Input](screenshots/01_input.png)

### Round Robin Results
<!-- Replace the line below with your screenshot -->
![Round Robin Results](screenshots/02_rr_results.png)

### SRTF Results
<!-- Replace the line below with your screenshot -->
![SRTF Results](screenshots/03_srtf_results.png)

### Comparison Summary
<!-- Replace the line below with your screenshot -->
![Comparison Summary](screenshots/04_comparison.png)

### Analysis & Conclusion
<!-- Replace the line below with your screenshot -->
![Analysis & Conclusion](screenshots/05_conclusion.png)

---

## 📁 Project Structure

```
scheduling/
├── Process.java              # Data model — stores all process attributes
├── GanttSegment.java         # Data model — one block in the Gantt chart
├── SimulationResult.java     # Data model — bundles procs + gantt + queue log
├── SchedulingAlgorithms.java # Core logic — runRR() and runSRTF()
├── GanttChartPanel.java      # Custom JPanel — draws the Gantt chart
├── ResultRenderer.java       # UI helper — fills a result tab (table + gantt + queue)
├── ComparisonPanel.java      # UI helper — side-by-side table + conclusion text
└── SchedulingProjectC2.java  # Main JFrame — input panel, scenario loader, entry point
```

---

## ⚙️ How to Run

### Requirements
- Java **JDK 8** or higher
- Any terminal / command prompt

### Steps

```bash
# 1. Navigate to the project folder
cd scheduling

# 2. Compile all files
javac *.java

# 3. Run the application
java SchedulingProjectC2
```

---

## 🔬 Features

| Feature | Description |
|---|---|
| ➕ Add Process | Enter PID, Arrival Time, and Burst Time manually |
| ✅ Input Validation | Duplicate PIDs, negative arrival, zero burst — all caught with clear error messages |
| 🎬 Scenarios A–E | One-click test cases covering mixed loads, quantum sensitivity, short-job bursts, fairness, and validation demos |
| 📊 Gantt Chart | Color-coded proportional chart rendered per algorithm |
| 📋 Ready-Queue Log | Step-by-step queue snapshot for every Round Robin dispatch |
| 🏆 Comparison Tab | Side-by-side metrics table with an automatic Winner column |
| 📝 Conclusion Tab | Answers all 6 analysis questions with the actual simulation numbers |

---

## 🧪 Test Scenarios

| Scenario | Processes | Quantum | Purpose |
|---|---|---|---|
| **A — Basic Mixed** | P1(0,8), P2(1,4), P3(2,9), P4(3,5) | 3 | General comparison baseline |
| **B — Quantum Sensitivity** | P1(0,20), P2(0,20) | 10 | Effect of a large quantum on RR |
| **C — Short-job Heavy** | P1(0,20), P2(1,2), P3(2,2) | 2 | SRTF advantage with short bursts |
| **D — Interactive Fairness** | P1(0,10), P2(0,10), P3(0,10) | 1 | RR fairness with equal processes |
| **E — Validation** | Invalid inputs → recovery | — | Input guard validation demo |

---

## 📈 Algorithms

### Round Robin (RR)
- Each process gets a fixed **time quantum** before being preempted.
- Guarantees **fairness** — no process starves.
- Response time is predictable and bounded by `(n-1) × quantum`.
- Best for **interactive / real-time** systems.

### Shortest Remaining Time First (SRTF)
- Preemptive version of SJF — always runs the process with the **least remaining time**.
- Minimises average **waiting time** and **turnaround time**.
- May cause **starvation** for long processes under heavy load.
- Best for **batch / throughput-oriented** systems.

---

## 📊 Metrics Reported

| Metric | Formula |
|---|---|
| **Completion Time (CT)** | Time the process finishes |
| **Turnaround Time (TAT)** | CT − Arrival Time |
| **Waiting Time (WT)** | TAT − Burst Time |
| **Response Time (RT)** | First CPU start − Arrival Time |
