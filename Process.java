import java.util.List;

public class Process {
    public String id;
    public int arrivalTime, burstTime, remainingTime;
    public int completionTime, waitingTime, turnaroundTime, responseTime;
    public int firstStartTime = -1;

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

class GanttSegment {
    public String processId;
    public int startTime, endTime;

    public GanttSegment(String id, int start, int end) {
        this.processId = id;
        this.startTime = start;
        this.endTime   = end;
    }
}


class SimulationResult {
    public List<Process>     procs;
    public List<GanttSegment> gantt;
    public List<String>      queueSnapshots;

    public SimulationResult(List<Process> p, List<GanttSegment> g, List<String> q) {
        this.procs          = p;
        this.gantt          = g;
        this.queueSnapshots = q;
    }
}
