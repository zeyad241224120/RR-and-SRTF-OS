# CPU Scheduling Simulator — Round Robin vs SRTF

## 📌 Project Overview

This project is a Java Swing-based CPU Scheduling Simulator developed for an Operating Systems course project.

The simulator compares two important CPU scheduling algorithms:

* **Round Robin (RR)**
* **Shortest Remaining Time First (SRTF)**

The application allows users to:

* Add and manage processes dynamically.
* Simulate both algorithms on the same workload.
* Visualize execution using Gantt Charts.
* Compare performance metrics.
* Analyze fairness, waiting time, turnaround time, and response time.
* Test predefined scheduling scenarios.
* Validate invalid user inputs.

---

# 🎯 Project Objectives

The main objectives of this project are:

* Understand CPU scheduling concepts practically.
* Compare preemptive scheduling algorithms.
* Analyze scheduling performance metrics.
* Demonstrate fairness and responsiveness differences.
* Simulate realistic workloads.
* Provide visual and statistical comparison.

---

# ⚙️ Implemented Algorithms

## 1️⃣ Round Robin (RR)

Round Robin is a preemptive scheduling algorithm where each process receives a fixed CPU time slice called **Quantum**.

### Features

* Fair CPU sharing.
* Prevents starvation.
* Suitable for interactive systems.
* Uses Ready Queue rotation.

### Advantages

* High fairness.
* Good response time.
* Simple implementation.

### Disadvantages

* Context switching overhead.
* Performance depends heavily on Quantum size.

---

## 2️⃣ Shortest Remaining Time First (SRTF)

SRTF is the preemptive version of Shortest Job First (SJF).

### Features

* Always executes the process with the shortest remaining burst time.
* Minimizes waiting time.
* Optimizes turnaround time.

### Advantages

* Excellent average waiting time.
* Excellent turnaround performance.
* Efficient for batch systems.

### Disadvantages

* May cause starvation for long processes.
* Requires frequent preemption.
* Higher context switching.

---

# 🖥️ GUI Features

The application contains a complete graphical interface using Java Swing.

## Main Features

### ✅ Process Input Panel

Users can:

* Enter Process ID.
* Enter Arrival Time.
* Enter Burst Time.
* Set Round Robin Quantum.

### ✅ Workload Table

Displays all inserted processes.

### ✅ Simulation Tabs

Separate tabs for:

* Round Robin Results
* SRTF Results
* Comparison Summary
* Analysis & Conclusion

### ✅ Gantt Charts

Visual representation of process execution order.

### ✅ Ready Queue Snapshots

Displays queue state during Round Robin execution.

### ✅ Validation System

The system validates:

* Empty PID
* Duplicate PID
* Negative Arrival Time
* Zero/Negative Burst Time
* Invalid Quantum
* Non-numeric input

---

# 📊 Performance Metrics

The simulator calculates:

| Metric                | Description                       |
| --------------------- | --------------------------------- |
| Waiting Time (WT)     | Time process waits in ready queue |
| Turnaround Time (TAT) | Completion Time - Arrival Time    |
| Response Time (RT)    | First CPU response - Arrival Time |
| Completion Time       | Final execution finish time       |

The program also computes:

* Average Waiting Time
* Average Turnaround Time
* Average Response Time

---

# 🧪 Required Test Scenarios

The project includes five predefined scenarios.

---

## Scenario A — Basic Mixed Workload

### Purpose

Tests general scheduling behavior using mixed burst times.

### Processes

| Process | Arrival | Burst |
| ------- | ------- | ----- |
| P1      | 0       | 8     |
| P2      | 1       | 4     |
| P3      | 2       | 9     |
| P4      | 3       | 5     |

### Goal

* Compare average waiting time.
* Observe execution order.
* Compare fairness.
  
<div align="center">
  <img src="https://github.com/user-attachments/assets/5e4d198c-1342-408b-a9e2-33097ffc0145" width="45%" />
  <img src="https://github.com/user-attachments/assets/ec681549-9d34-4682-8db1-ab2c8285cb03" width="45%" />
  <br>
  <br>
  <img src="https://github.com/user-attachments/assets/b301435b-c69e-4d1f-b133-58f989dda9b0" width="45%" />
  <img src="https://github.com/user-attachments/assets/21098b78-e678-4a7d-8006-231ca6f83085" width="45%" />
</div>
---

## Scenario B — Quantum Sensitivity

### Purpose

Shows how quantum size affects Round Robin performance.

### Processes

| Process | Arrival | Burst |
| ------- | ------- | ----- |
| P1      | 0       | 20    |
| P2      | 0       | 20    |

### Goal

* Analyze quantum effect.
* Observe context switching.
* Compare efficiency.

### Screenshots

<div align="center">
  <img src="https://github.com/user-attachments/assets/74f90439-b259-4e0e-b368-99a181e6c03e" width="45%" />
  <img src="https://github.com/user-attachments/assets/903e5ae4-a1b3-4187-ba05-15e0ffd40c8b" width="45%" />
  <br>
  <br>
  <img src="https://github.com/user-attachments/assets/8aa447bb-5876-48f5-a803-1513e2d9dc86" width="45%" />
  <img src="https://github.com/user-attachments/assets/8ef15a28-467c-4200-9f2f-c569e1da55c8" width="45%" />
</div>

---

## Scenario C — Short-Job Heavy

### Purpose

Demonstrates SRTF optimization for short processes.

### Processes

| Process | Arrival | Burst |
| ------- | ------- | ----- |
| P1      | 0       | 20    |
| P2      | 1       | 2     |
| P3      | 2       | 2     |

### Goal

* Observe SRTF preemption.
* Compare waiting times.
* Analyze short-job completion speed.

### Screenshots

<div align="center">
  <img src="https://github.com/user-attachments/assets/3822f203-aa86-4700-8099-9921b3724ed7" width="45%" />
  <img src="https://github.com/user-attachments/assets/eb6be247-b914-4ec6-bf35-0f99e3e6ff06" width="45%" />
  <br>
  <br>
  <img src="https://github.com/user-attachments/assets/7e4bd6a4-372a-4800-98f0-dd7131307280" width="45%" />
  <img src="https://github.com/user-attachments/assets/626a2797-8642-45b1-8a52-e526aefef4c6" width="45%" />
</div>
---

## Scenario D — Interactive Fairness

### Purpose

Evaluates fairness between equal-length processes.

### Processes

| Process | Arrival | Burst |
| ------- | ------- | ----- |
| P1      | 0       | 10    |
| P2      | 0       | 10    |
| P3      | 0       | 10    |

### Goal

* Analyze fairness.
* Observe CPU sharing.
* Compare responsiveness.

### Screenshots

<div align="center">
  <img src="https://github.com/user-attachments/assets/798e1e35-ec90-4f3d-ad8f-e6acc08a6ac8" width="45%" />
  <img src="https://github.com/user-attachments/assets/db927e0d-a954-43db-89de-c1439aa58eb6" width="45%" />
  <br>
  <br>
  <img src="https://github.com/user-attachments/assets/e86cb7a7-da69-44a8-875d-49b2378fa91c" width="45%" />
  <img src="https://github.com/user-attachments/assets/29220c20-d209-4989-b5b6-c96e624f2040" width="45%" />
</div>
---

## Scenario E — Validation Case

### Purpose

Tests user input validation.

### Invalid Cases

* Negative Arrival Time
* Zero Burst Time
* Empty PID
* duplicate PID 

### Goal

* Ensure robust validation.
* Prevent invalid simulation states.
* Verify system recovery.

### Screenshots

<div align="center">
  <img src="https://github.com/user-attachments/assets/15ae3657-b0c8-4c42-b4db-80d61c084665" width="45%" />
  <img src="https://github.com/user-attachments/assets/5fdaf11c-61cb-4d8a-94b0-9257db78887a" width="45%" />
  <br>
  <br>
  <img src="https://github.com/user-attachments/assets/8be42718-e755-4b18-8ae1-8e928cc15aa9" width="45%" />
  <img src="https://github.com/user-attachments/assets/811e2c12-3abe-4b7d-b902-86f06ff3c631" width="45%" />
  <br>
  <img src="https://github.com/user-attachments/assets/43c37bff-3a3c-417f-9ed8-79763c843ae8" width="45%" />
</div>
---

# 📈 Comparison Summary

The simulator compares:

| Feature             | Round Robin | SRTF       |
| ------------------- | ----------- | ---------- |
| Fairness            | High        | Medium/Low |
| Starvation Risk     | No          | Yes        |
| Waiting Time        | Moderate    | Excellent  |
| Response Time       | Excellent   | Good       |
| Throughput          | Moderate    | High       |
| Context Switching   | Moderate    | High       |
| Interactive Systems | Excellent   | Poor       |
| Batch Systems       | Good        | Excellent  |

---

# 🧠 Analysis & Conclusion

The simulator automatically generates conclusions answering:

1. Which algorithm produced better waiting time?
2. Which algorithm produced better response time?
3. Was Round Robin fairer?
4. Did SRTF complete short jobs faster?
5. How did quantum affect Round Robin?
6. Which algorithm is recommended for the workload?

---

# 🏗️ Project Structure

```text

├── Main.java
├── InputPanel.java
├── Process.java
├── ResultRenderer.java
├── RoundRobinAlgorithm.java
├── SchedulingProjectC2.java
├── SRTFAlgorithm.java
```

# 📌 Example Workflow

1. Add processes manually.
2. Set quantum value.
3. Click **Simulate Both**.
4. Observe:

   * Scheduling tables
   * Gantt charts
   * Queue snapshots
   * Comparison summary
   * Final analysis

---

# ✅ Key Features Implemented

* ✔ Round Robin Scheduler
* ✔ SRTF Scheduler
* ✔ GUI Interface
* ✔ Gantt Charts
* ✔ Queue Visualization
* ✔ Performance Metrics
* ✔ Scenario Testing
* ✔ Validation Handling
* ✔ Comparison Summary
* ✔ Automatic Analysis
