import java.util.*;

// ═══════════════════════════════════════════════
//  FILE 3 — RoundRobinAlgorithm.java
//  المسؤول: الشخص الثالث
//  الهدف:   خوارزمية Round Robin كاملة
// ═══════════════════════════════════════════════

public class RoundRobinAlgorithm {

    /**
     * تشغيل خوارزمية Round Robin
     *
     * @param procs  قائمة العمليات (مُستنسخة مسبقاً)
     * @param q      الـ Time Quantum
     * @return       SimulationResult يحتوي على النتائج + Gantt + Queue Snapshots
     */
    public static SimulationResult run(List<Process> procs, int q) {

        List<GanttSegment> gantt          = new ArrayList<>();
        List<String>       queueSnapshots = new ArrayList<>();
        Queue<Process>     readyQueue     = new LinkedList<>();

        int time = 0, completed = 0, n = procs.size();

        // ترتيب العمليات حسب وقت الوصول
        procs.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int idx = 0;

        while (completed < n) {

            // إضافة العمليات التي وصلت حتى الآن إلى الـ Ready Queue
            while (idx < n && procs.get(idx).arrivalTime <= time)
                readyQueue.add(procs.get(idx++));

            // إذا كانت الـ Queue فارغة → CPU IDLE
            if (readyQueue.isEmpty()) {
                queueSnapshots.add("t=" + time + ": Queue = [] → CPU IDLE");
                gantt.add(new GanttSegment("IDLE", time, ++time));
                continue;
            }

            // لقطة الـ Queue قبل السحب
            StringBuilder sb = new StringBuilder("t=" + time + ": Queue = [");
            List<Process> snap = new ArrayList<>(readyQueue);
            for (int i = 0; i < snap.size(); i++) {
                sb.append(snap.get(i).id)
                  .append("(rem=").append(snap.get(i).remainingTime).append(")");
                if (i < snap.size() - 1) sb.append(", ");
            }

            // سحب العملية من أول الـ Queue
            Process curr = readyQueue.poll();
            sb.append("] → Dispatching: ").append(curr.id);
            queueSnapshots.add(sb.toString());

            // تسجيل أول مرة تبدأ فيها العملية
            if (curr.firstStartTime == -1) {
                curr.firstStartTime = time;
                curr.responseTime   = time - curr.arrivalTime;
            }

            // تشغيل العملية لمدة Quantum أو ما تبقى منها
            int burst = Math.min(curr.remainingTime, q);
            gantt.add(new GanttSegment(curr.id, time, time + burst));

            for (int i = 0; i < burst; i++) {
                time++;
                // إضافة أي عمليات وصلت أثناء التشغيل
                while (idx < n && procs.get(idx).arrivalTime <= time)
                    readyQueue.add(procs.get(idx++));
            }

            curr.remainingTime -= burst;

            if (curr.remainingTime > 0) {
                // لم تنته → ترجع لآخر الـ Queue
                readyQueue.add(curr);
            } else {
                // انتهت → تحسب أوقاتها
                completed++;
                curr.completionTime = time;
                curr.turnaroundTime = time - curr.arrivalTime;
                curr.waitingTime    = curr.turnaroundTime - curr.burstTime;
            }
        }

        return new SimulationResult(procs, gantt, queueSnapshots);
    }
}