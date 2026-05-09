import java.util.*;

public class RoundRobinAlgorithm {

  
    public static SimulationResult run(List<Process> procs, int q) {

        List<GanttSegment> gantt          = new ArrayList<>();
        List<String>       queueSnapshots = new ArrayList<>();
        Queue<Process>     readyQueue     = new LinkedList<>();

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
                curr.completionTime = time;
                curr.turnaroundTime = time - curr.arrivalTime;
                curr.waitingTime    = curr.turnaroundTime - curr.burstTime;
            }
        }

        return new SimulationResult(procs, gantt, queueSnapshots);
    }
}
