import java.util.*;

public class SRTFAlgorithm {

  
    public static SimulationResult run(List<Process> procs) {

        List<GanttSegment> gantt = new ArrayList<>();
        int time = 0, completed = 0, n = procs.size();
        String last = "";

        while (completed < n) {

            Process shortest = null;
            int min = Integer.MAX_VALUE;

            for (Process p : procs) {
                if (p.arrivalTime <= time && p.remainingTime > 0 && p.remainingTime < min) {
                    min      = p.remainingTime;
                    shortest = p;
                }
            }

            if (shortest == null) {
                gantt.add(new GanttSegment("IDLE", time, ++time));
                last = "IDLE";
                continue;
            }

            if (shortest.firstStartTime == -1) {
                shortest.firstStartTime = time;
                shortest.responseTime   = time - shortest.arrivalTime;
            }

            if (last.equals(shortest.id)) {
                gantt.get(gantt.size() - 1).endTime++;
            } else {
                gantt.add(new GanttSegment(shortest.id, time, time + 1));
            }

            shortest.remainingTime--;
            time++;
            last = shortest.id;

            if (shortest.remainingTime == 0) {
                completed++;
                shortest.completionTime = time;
                shortest.turnaroundTime = time - shortest.arrivalTime;
                shortest.waitingTime    = shortest.turnaroundTime - shortest.burstTime;
            }
        }

        return new SimulationResult(procs, gantt, new ArrayList<>());
    }
}
