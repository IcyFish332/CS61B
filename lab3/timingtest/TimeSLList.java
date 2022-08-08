package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        SLList<Integer> N = new SLList<Integer>();
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();
        Stopwatch sw;
        int num = 0;
        int M = 10000;

        for (int i = 0; i < 128000; i++) {
            N.addLast(i);
            if (i == Math.pow(2,num) * 1000 - 1) {
                sw = new Stopwatch();
                for (int j = 0; j < M; j += 1){
                    N.getLast();
                }
                Ns.addLast(N.size());
                times.addLast(sw.elapsedTime());
                opCounts.addLast(M);
                num++;
            }
        }
        printTimingTable(Ns, times, opCounts);
        // TODO: YOUR CODE HERE
    }

}
