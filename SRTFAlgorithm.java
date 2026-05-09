import java.util.*;

// ═══════════════════════════════════════════════
//  FILE 4 — SRTFAlgorithm.java
//  المسؤول: الشخص الرابع
//  الهدف:   خوارزمية SRTF (Shortest Remaining Time First)
// ═══════════════════════════════════════════════

public class SRTFAlgorithm {

    /**
     * تشغيل خوارزمية SRTF (Preemptive SJF)
     *
     * @param procs  قائمة العمليات (مُستنسخة مسبقاً)
     * @return       SimulationResult يحتوي على النتائج + Gantt Chart
     */
    public static SimulationResult run(List<Process> procs) {

        List<GanttSegment> gantt = new ArrayList<>();
        int time = 0, completed = 0, n = procs.size();
        String last = "";

        while (completed < n) {

            // ابحث عن العملية ذات أقصر وقت متبقٍّ
            Process shortest = null;
            int min = Integer.MAX_VALUE;

            for (Process p : procs) {
                if (p.arrivalTime <= time && p.remainingTime > 0 && p.remainingTime < min) {
                    min      = p.remainingTime;
                    shortest = p;
                }
            }

            // لا توجد عملية جاهزة → CPU IDLE
            if (shortest == null) {
                gantt.add(new GanttSegment("IDLE", time, ++time));
                last = "IDLE";
                continue;
            }

            // تسجيل أول مرة تبدأ فيها العملية
            if (shortest.firstStartTime == -1) {
                shortest.firstStartTime = time;
                shortest.responseTime   = time - shortest.arrivalTime;
            }

            // تمديد آخر Segment إذا كانت نفس العملية، أو إنشاء segment جديد
            if (last.equals(shortest.id)) {
                gantt.get(gantt.size() - 1).endTime++;
            } else {
                gantt.add(new GanttSegment(shortest.id, time, time + 1));
            }

            // تشغيل وحدة زمنية واحدة
            shortest.remainingTime--;
            time++;
            last = shortest.id;

            // تحقق من الانتهاء
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